# Sessions
Stytch user sessions are identified by a `SessionData` or `B2BSessionData` object, a session token, and a JWT (JSON Web Token) that are authenticated on, and returned from, our authentication endpoints. The Stytch Android SDK automatically persists these tokens and appends them to network requests as required, to make interacting with Stytch's authentication flows as simple as possible, and provides both Automatic and Manual session management helpers.

## Session Data Persistence
The Stytch Android SDK persists to `SharedPreferences` a few sets of encrypted values (using AES256-GCM) which are accessible across launches and help with session management:
1. The session token and JWT.
2. For the consumer client: `SessionData` and `UserData`.
3. For the B2B client: `B2BSessionData`, `MemberData` and `OrganizationData`.

The `SessionData` / `B2BSessionData` object is the definitive source of truth for if the user is logged in or not as it contains an expiration date property for when the session expires.

## Automatic Session Management
When the Stytch Android SDK is initialized, it decrypts the persisted session tokens and `SessionData` / `B2BSessionData` object, if any, and attempts to make a Sessions Authenticate call to ensure the session is still active/valid. On every authentication response, the SDK updates the in-memory and device-persisted session data with the latest data returned from the endpoint. In addition, once the SDK receives a valid session, it begins an automatic "heartbeat" job that checks for continued session validity in the background, roughly every three minutes. This heartbeat does not extend an existing session, it merely checks it's validity and updates the local data as appropriate. You as the developer can either observe changes in the session via the `onChange` flow/callback shown below, or directly though the `StytchClient.sessions.session` which both access the Session object stored in `SharedPreferences`.

It is then good practice when using the Stytch Android SDK to access any data that may have been updated via the heartbeat call through their cached values. Otherwise if you hold onto a reference of a response that contains authentication information in it, like token or user, you risk the those values returned in the response becoming stale. Values updated via the heartbeat are: `SessionData`, `UserData`, `B2BSessionData`, `MemberData` `OrganizationData`, `sessionToken` and  `sessionJwt`. 

Examples for retrieving these cached values:
```kotlin
// tokens
val sessionToken: String? = StytchClient.sessions.sessionToken
val sessionJwt: String? = StytchClient.sessions.sessionJwt

// consumer objects
val sessionData: SessionData? = StytchClient.sessions.getSync()
val userData: UserData? = StytchClient.user.getSyncUser()
```

### Observing Stytch Object Information

To unify the publishing of various Stytch object types such as `SessionData`, `UserData`, `B2BSessionData`, `MemberData`, and `OrganizationData`, the SDK provides the `StytchObjectInfo<T>` type. This generic type handles the publishing of objects in a way that avoids having to publish null values.
* If an object is available for publishing, `StytchObjectInfo.Available` is emitted, containing a `value` property of type `T` representing the desired data and a `lastValidatedAt` property, representing the last time that the data was validated with Stytch's servers. The receiver can then determine if the object is within their acceptable time tolerance for use.
* If there is no object to publish, `StytchObjectInfo.Unavailable` will be emitted instead. This ensures that the flow/callback never needs to send a null value.
```kotlin
public sealed interface StytchObjectInfo<out T> {
    public data object Unavailable : StytchObjectInfo<Nothing>

    public data class Available<out T>(
        val lastValidatedAt: Date,
        val value: T,
    ) : StytchObjectInfo<T>
}
```
Each of the five object types—`SessionData`, `UserData`, `B2BSessionData`, `MemberData`, and `OrganizationData` has its own dedicated `onChange` flow/callback, ensuring that state changes for each type are handled individually. In the example below, we show the session flow/callback, but similar flows/callbacks exist for each of the other object types. This mechanism simplifies state management and ensures clean, consistent data flow throughout your app when interacting with these Stytch objects.

For session state changes specifically, you can subscribe to the `onChange` flow/callback:
```kotlin
// Flow:
viewModelScope.launch {
    StytchClient.sessions.onChange.collect {
        // it: StytchObjectInfo<SessionData>
        when(it) {
            is StytchObject.Available -> println("User has an active session")
            is StytchObject.Unavailable -> println("No active session")
        }
    }
}

// Callback:
StytchClient.sessions.onChange {
    // it: StytchObjectInfo<SessionData>
    when(it) {
        is StytchObject.Available -> println("User has an active session")
        is StytchObject.Unavailable -> println("No active session")
    }
}
```
## Creating or Extending a Session
On all authentication requests, you can pass an optional parameter indicating the length of time a session should be valid for. This will be validated on the Stytch servers to ensure that it is within the minimum and maximum values configured in your Stytch dashboard (between 5 minutes and 1 year). 

Every authentication call that supplies a session duration (and succeeds!) will either create a session (if none exists), or extend the session duration by that length of time (if there is an active session).

With the exception of Sessions Authenticate calls, if you do not provide a session duration, the SDK will default it to 5 minutes. The Sessions Authenticate call is special, in that there is no default session duration if none is passed. This enables the "heartbeat" functionality discussed earlier. 

If you call authenticate with no `sessionDurationMinutes` it will merely respond with whether or not the session is active.
```kotlin
StytchClient.sessions.authenticate(Sessions.AuthParams())
```
If you do pass in a `sessionDurationMinutes` it will behave like all other endpoints and extend the existing session by 5 minutes.
```kotlin
StytchClient.sessions.authenticate(Sessions.AuthParams(sessionDurationMinutes = 5))
``` 

## Manual Session Management
The [Sessions client](../source/sdk/src/main/java/com/stytch/sdk/consumer/sessions/Sessions.kt) provides an interface for managing the session.

Authenticating and Revoking Sessions:
```kotlin
// Authenticate
val authenticateResponse = StytchClient.sessions.authenticate(Sessions.AuthParams())

// Revoke - clears all values for `Session`, `User`, `MemberSession`, `Member` `Organization`, `sessionToken` and  `sessionJwt`
val revokeResponse = StytchClient.sessions.revoke(Sessions.RevokeParams())
```
Updating a session with tokens retrieved outside of the SDK (for instance, if you create or update a session on your backend, and want to hydrate a client application) can be done using the `updateSession` method:
```kotlin
// update the local tokens
StytchClient.sessions.updateSession(sessionToken: "sessionToken", sessionJwt: "sessionJwt")
// Authenticate with the new tokens
val authenticateResponse = StytchClient.sessions.authenticate(Sessions.AuthParams())
```
## Further Reading
For more information on the Stytch Sessions product, consult our [sessions guide](https://stytch.com/docs/guides/sessions/using-sessions).