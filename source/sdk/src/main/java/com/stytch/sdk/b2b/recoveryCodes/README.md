# Package com.stytch.sdk.b2b.recoveryCodes
The [RecoveryCodes](RecoveryCodes.kt) interface provides methods for getting, rotating, and using recovery codes for a member

Call the `StytchB2BClient.recoveryCodes.get()` method to retrieve an authenticated members recovery codes.

Call the `StytchB2BClient.recoveryCodes.rotate()` method to rotate an authenticated members recovery codes

Call the `StytchB2BClient.recoveryCodes.recover()` method to authenticate a member using a recovery code.

