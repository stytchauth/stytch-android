# Package com.stytch.sdk.common.oauth
Contains shared OAuth implementation code.

Our Third Party OAuth flow follows the model of [AppAuth-Android](https://github.com/openid/AppAuth-Android/)

It consists of a "manager" activity that handles the state management of the OAuth flow and starting
the authentication flow in the user's browser, and a "receiver" activity which handles receiving the
authentication deeplink. By using this manager/receiver pattern we can ensure better control over
the backstack, preventing navigation from keeping authorization activities in the backstack.


The below diagram explains more about the manager/receiver activities and how they relate to the backstack:

 ```unset
 
                           Back Stack Towards Top
                 +------------------------------------------>
 
  +------------+            +---------------+      +----------------+      +--------------+
  |            |     (1)    |               | (2)  |                | (S1) |              |
  | Initiating +----------->| OAuthManager  +----->| Authorization  +----->| OAuthReceiver|
  |  Activity  |            |   Activity    |      |   Activity     |      |   Activity   |
  |            |<-----------+               |<-----+ (e.g. browser) |      |              |
  |            | (S3, C2)   |               | (C1) |                |      |              |
  +------------+            +-------+-------+      +----------------+      +-------+------+
                                    ^                                              |
                                    |                   (S2)                       |
                                    +----------------------------------------------+
 
  - Step 1: ThirdPartyOAuth intiates an intent which launches this (no-ui) activity
  - Step 2: This activity determines the best browser to launch the authorization flow in, and launches it. Depending
    on user action, we then enter either a cancellation (C)  or success (S) flow
 
  Cancellation (C) flow:
  If the user cancels the authorization, we are returned to this Activity at the top of the backstack (C1). Since no
  return URI is provided, we know the user cancelled, and return a RESULT_CANCELED result for the original intent (C2)
  and finish the activity. The calling activity will listen for this result and either provide messaging for the user
  if the error returned is one of NO_BROWSER_FOUND or NO_URI_FOUND, or (most likely) do nothing if it is USER_CANCELED.
 
  Success (S) flow:
  When the user completes authorization, the OAuthReceiverActivity is launched (S1), as specified in the manifest. That
  activity will launch this activity (S2) via an intent with CLEAR_TOP set, so that the authorization activity and
  receiver activity are destroyed leaving this activity at the top of the backstack. This activity will then return a
  RESULT_OK status for the original intent and pass along the returned URI, then finish itself (S3). The calling
  activity will listen for this result and use the returned URI to make the authorization call to the Stytch API.
 ```