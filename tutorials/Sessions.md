# Sessions
Stytch user sessions are identified by a session token and a JWT (JSON Web Token) that are authenticated on, and returned from, our authentication endpoints. The Stytch Android SDK automatically persists these tokens and appends them to network requests as required, to make interacting with Stytch's authentication flows as simple as possible, and provides both Automatic and Manual session management helpers.

## SDK Sessions At-A-Glance
### Session Data Persistence
The Stytch Android SDK persists the session token and JWT to device by saving them to `SharedPreferences`, after encrypting them using AES256-GCM. The actual session and user data is stored in-memory only.

### Automatic Session Management
When the Stytch Android SDK is initialized, it decrypts the persisted session token, if any, and makes a Sessions Authenticate call to ensure the session is still active/valid. If it is, it will automatically rehydrate the session and user data in memory; if not, it clears all persisted session tokens.

On every authentication response, the SDK updates the in-memory and device-persisted session data with the latest data returned from the endpoint. In addition, once the SDK receives a valid session, it begins an automatic "heartbeat" job that checks for continued session validity in the background, roughly every three minutes. This heartbeat does not extend an existing session, it merely checks it's validity and updates the local data as appropriate.

### Manual Session Management and Observation
The [Sessions client](../source/sdk/src/main/java/com/stytch/sdk/consumer/sessions/Sessions.kt) provides properties to retrieve the current session tokens; methods for authenticating, updating, and revoking sessions; and a flow/callback to listen for changes in session state.

To retrieve any existing session data, access the appropriate property or method, which will return the decrypted value to you, if it exists. This may be useful if you need to parse a JWT or use the token for a call from your backend, or need access to the in-memory session data:
```kotlin
val sessionToken: String? = StytchClient.sessions.sessionToken
val sessionJwt: String? = StytchClient.sessions.sessionJwt
val sessionData: SessionData? = StytchClient.sessions.getSync()
```
Authenticating and Revoking sessions are similarly easy:
```kotlin
val authResponse = StytchClient.sessions.authenticate(Sessions.AuthParams())
val revokeResponse = StytchClient.sessions.revoke(Sessions.RevokeParams())
```
Updating a session with tokens retrieved outside of the SDK (for instance, if you create or update a session on your backend, and want to hydrate a client application) can be done using the `updateSession` method:
```kotlin
StytchClient.sessions.updateSession(
    sessionToken="my-session-token",
    sessionJwt="my-session-jwt"
)
```
Lastly, to listen for session state changes, you can either subscribe to the `onChange` flow, or provide a callback to the `onChange` method:
```kotlin
viewModelScope.launch {
    StytchClient.sessions.onChange.collect {
        // it: SessionData?
        when(it) {
            null -> println("No active session")
            else -> println("User has an active session")
        }
    }
}
```
```kotlin
StytchClient.sessions.onChange {
    // it: SessionData?
    when(it) {
        null -> println("No active session")
        else -> println("User has an active session")
    }
}
```

## Creating or Extending a Session
On all authentication requests, you can pass an optional parameter indicating the length of time a session should be valid for. This will be validated on the Stytch servers to ensure that it is within the minimum and maximum values configured in your Stytch dashboard (between 5 minutes and 1 year). 

Every authentication call that supplies a session duration (and succeeds!) will either create a session (if none exists), or extend the session duration by that length of time (if there is an active session).

With the exception of Sessions Authenticate calls, if you do not provide a session duration, the SDK will default it to 5 minutes. The Sessions Authenticate call is special, in that there is no default session duration if none is passed. This enables the "heartbeat" functionality discussed earlier. If you call `StytchClient.sessions.authenticate(Sessions.AuthParams())`, it will merely respond with whether or not the session is active; if you pass in `Sessions.AuthParams(sessionDurationMinutes = 5))`, it will behave like all other endpoints and extend the existing session by 5 minutes.

## Further Reading
For more information on the Stytch Sessions product, consult our [sessions guide](https://stytch.com/docs/guides/sessions/using-sessions).