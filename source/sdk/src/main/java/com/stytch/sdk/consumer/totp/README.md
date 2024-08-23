# Package com.stytch.sdk.consumer.totp
The [TOTP](TOTP.kt) interface provides methods for creating, authenticating, and recovering TOTPs for a user

Call the `StytchClient.totp.create()` method to create a new TOTP registration for a user

Call the `StytchClient.totp.authenticate()` method to authenticate a TOTP code for a user

Call the `StytchClient.totp.recoveryCodes()` method to retrieve the recovery codes for a user

Call the `StytchClient.totp.recover()` method to authenticate a recovery code for a user
