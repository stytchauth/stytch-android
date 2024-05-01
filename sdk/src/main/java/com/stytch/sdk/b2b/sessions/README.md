# Package com.stytch.sdk.b2b.sessions
The [B2BSessions](B2BSessions.kt) interface provides methods for authenticating, updating, or revoking sessions, and properties to retrieve the existing session token (opaque or JWT).

Call the `StytchB2BClient.sessions.authenticate()` method to authenticate a Session and (optionally) update its lifetime by the specified sessionDurationMinutes. If the sessionDurationMinutes is not specified, a Session will not be extended.

Call the `StytchB2BClient.sessions.updateSession()` method to update the existing session with new session data.

Call the `StytchB2BClient.sessions.revoke()` method to revoke the current session and immediately invalidate all of its tokens.

Call the `StytchB2BClient.sessions.onChange()` method with a callback to be triggered on changes to the underlying session. Alternatively, you can listen directly to the flow using the `StytchB2BClient.sessions.onChange` StateFlow.
