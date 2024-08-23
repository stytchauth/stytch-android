# Package com.stytch.sdk.b2b.otp
The [OTP](OTP.kt) interface provides methods for sending and authenticating a one-time passcode (OTP). Currently, only SMS OTP are supported.

Call the `StytchB2BClient.otp.sms.send()` method to send a one-time passcode (OTP) to a user using their phone number via SMS.

Call the `StytchB2BClient.otp.sms.authenticate()` method to authenticate a one-time passcode (OTP) for a user.
