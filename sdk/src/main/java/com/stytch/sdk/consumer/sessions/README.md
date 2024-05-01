# Package com.stytch.sdk.consumer.sessions
The [Sessions](Sessions.kt) interface provides methods for authenticating, updating, or revoking sessions, and properties to retrieve the existing session token (opaque or JWT).

Call the `StytchClient.sessions.authenticate()` method to authenticate a Session and (optionally) update its lifetime by the specified sessionDurationMinutes. If the sessionDurationMinutes is not specified, a Session will not be extended.

Call the `StytchClient.sessions.updateSession()` method to update the existing session with new session data.

Call the `StytchClient.sessions.revoke()` method to revoke the current session and immediately invalidate all of its tokens.

Call the `StytchClient.sessions.onChange()` method with a callback to be triggered on changes to the underlying session. Alternatively, you can listen directly to the flow using the `StytchClient.sessions.onChange` StateFlow.
