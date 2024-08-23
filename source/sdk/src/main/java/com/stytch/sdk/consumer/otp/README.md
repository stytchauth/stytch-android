# Package com.stytch.sdk.consumer.otp
The [OTP](OTP.kt) interface provides methods for sending and authenticating One-Time Passcodes (OTP) via SMS, WhatsApp, and Email.

Call the `StytchClient.otp.[PLATFORM].loginOrCreate()` method (for your desired platform: `sms`, `whatsapp`, or `email`) to send a one-time passcode (OTP) to a user using their phone number or email address. If the phone number or email address is not associated with a user already, a user will be created. Make sure to persist the returned phone or email ID for use when authenticating an OTP.

Call the `StytchClient.otp.[PLATFORM].send()` method (for your desired platform: `sms`, `whatsapp`, or `email`) to send a one-time passcode (OTP) to an existing user using their phone number or email address. Make sure to persist the returned phone or email ID for use when authenticating an OTP.

Call the `StytchClient.otp.authenticate()` method to authenticate a provided OTP. Remember to include the phone or email ID returned from the `loginOrCreate()` or `send()` response.