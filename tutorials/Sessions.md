# Sessions
Stytch user sessions are identified by a session token and a JWT (JSON Web Token) that are authenticated on, and returned from, our authentication endpoints. The Stytch Android SDK automatically persists these tokens and appends them to network requests as required, to make interacting with Stytch's authentication flows as simple as possible, and provides both Automatic and Manual session management helpers.

## SDK Sessions At-A-Glance
### Session Data Persistence
The Stytch Android SDK persists the session token, session JWT, session data, and (for Consumer projects) user data, and (for B2B projects) member and organization data, to device by saving them to `SharedPreferences`, after encrypting them using AES256-GCM. This ensures that the data is accessible across app launches.

The Session object serves as the definitive source of truth for determining whether a user is logged in, as it contains an expiration date indicating when the session expires. On startup, and through a periodic "heartbeat" process, the SDK will attempt to call `StytchClient.sessions.authenticate()` roughly every three minutes to validate the session. As a developer, you can observe changes to these persisted states via a callback or flow (shown below), or directly access them via the appropriate methods on the relevant Stytch client (ie: `StytchClient.organizations.getSync()`).

### Automatic Session Management
When the Stytch Android SDK is initialized, it decrypts the persisted session token, if any, and makes a Sessions Authenticate call to ensure the session is still active/valid; if not, it clears all persisted session and user data.

On every authentication response, the SDK updates the device-persisted data with the latest data returned from the endpoint. In addition, once the SDK receives a valid session, it begins an automatic "heartbeat" job that checks for continued session validity in the background, roughly every three minutes. This heartbeat does not extend an existing session, it merely checks it's validity and updates the local data as appropriate.

Additionally, every time the persisted session data is accessed, an expiration date check is performed to ensure it is still valid (for instance, in case a session expires between heartbeat calls); if it is, persisted data is automatically deleted, and a `StytchObject.Unavailable` result is emitted to any flow/callback listeners.

#### Cached Data
Because the Stytch Android SDK performs automatic session and user syncing operations, it is not advised, or necessary, to cache the results of authentication requests yourself. Doing so risks caching and using stale data, which may be a source of bugs in your application. When you need access to session or user data, you should always retrieve them from the appropriate Stytch client, as outlined in the next section.

### Manual Session/User Management and Observation
The [Sessions client](../source/sdk/src/main/java/com/stytch/sdk/consumer/sessions/Sessions.kt) provides properties to retrieve the current session tokens; methods for authenticating, updating, and revoking sessions; and a flow/callback to listen for changes in session state. The [UserManagement client](../source/sdk/src/main/java/com/stytch/sdk/consumer/userManagement/UserManagement.kt) provides methods for retrieving the current user (if any); methods for updating the currently authenticated user; and a flow/callback to listen for changes in the user object.

To retrieve any existing session or user data, access the appropriate property or method, which will return the decrypted value to you, if it exists. This may be useful if you need to parse a JWT or use the token for a call from your backend, or need access to the in-memory session or user data:
```kotlin
val sessionToken: String? = StytchClient.sessions.sessionToken
val sessionJwt: String? = StytchClient.sessions.sessionJwt
val sessionData: SessionData? = StytchClient.sessions.getSync()
val userData: UserData? = StytchClient.user.getSyncUser()
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
Lastly, to listen for session or user state changes, you can either subscribe to the appropriate `onChange` flow, or provide a callback to the `onChange` method. These callbacks/flows will emit a `StytchObjectInfo<T>` result type when the underlying data changes. If the desired data exists, it will emit a `StytchObjectInfo<T>.Available` result, containing a `value` property (representing the desired data) and a `lastValidatedAt` property (representing the last time that the data was validated with Stytch's servers). If the data is not present, it will instead emit a `StytchObject.Unavailable` result.
```kotlin
viewModelScope.launch {
    StytchClient.sessions.onChange().collect {
        // it: StytchObjectInfo<SessionData>
        when(it) {
            is StytchObject.Available -> println("User has an active session")
            is StytchObject.Unavailable -> println("No active session")
        }
    }
}
viewModelScope.launch {
    StytchClient.user.onChange().collect {
        // it: StytchObjectInfo<UserData>
        when(it) {
            is StytchObject.Available -> println("User exists")
            is StytchObject.Unavailable -> println("There is no user")
        }
    }
}
```
```kotlin
StytchClient.sessions.onChange {
    // it: StytchObjectInfo<SessionData>
    when(it) {
        is StytchObject.Available -> println("User has an active session")
        is StytchObject.Unavailable -> println("No active session")
    }
}
StytchClient.user.onChange {
    // it: StytchObjectInfo<UserData>
    when(it) {
        is StytchObject.Available -> println("User exists")
        is StytchObject.Unavailable -> println("There is no user")
    }
}
```

## Creating or Extending a Session
On all authentication requests, you can pass an optional parameter indicating the length of time a session should be valid for. This will be validated on the Stytch servers to ensure that it is within the minimum and maximum values configured in your Stytch dashboard (between 5 minutes and 1 year). 

Every authentication call that supplies a session duration (and succeeds!) will either create a session (if none exists), or extend the session duration by that length of time (if there is an active session).

With the exception of Sessions Authenticate calls, if you do not provide a session duration, the SDK will default it to 5 minutes. The Sessions Authenticate call is special, in that there is no default session duration if none is passed. This enables the "heartbeat" functionality discussed earlier. If you call `StytchClient.sessions.authenticate(Sessions.AuthParams())`, it will merely respond with whether or not the session is active; if you pass in `Sessions.AuthParams(sessionDurationMinutes = 5))`, it will behave like all other endpoints and extend the existing session by 5 minutes.

## Further Reading
For more information on the Stytch Sessions product, consult our [sessions guide](https://stytch.com/docs/guides/sessions/using-sessions).