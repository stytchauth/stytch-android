# Package com.stytch.sdk.b2b.passwords
The [Passwords](Passwords.kt) interface provides methods for authenticating, creating, resetting, and performing strength checks of passwords.

Stytch supports creating, storing, and authenticating passwords, as well as support for account recovery (password reset) and account deduplication with passwordless login methods.

Our implementation of passwords has built-in breach detection powered by [HaveIBeenPwned](https://haveibeenpwned.com) on both sign-up and login, to prevent the use of compromised credentials and uses configurable strength requirements (either Dropbox’s [zxcvbn](https://github.com/dropbox/zxcvbn) or adjustable LUDS) to guide members towards creating passwords that are easy for humans to remember but difficult for computers to crack.

Call the `StytchB2BClient.passwords.authenticate()` method to authenticate a member with their email address and password. This method will return an error if the member’s credentials appeared in the HaveIBeenPwned dataset or if a duplicate member is found (eg: Email Magic Links, OAuth).

Call the `StytchB2BClient.passwords.resetByEmailStart()` method to initiate a password reset for the email address provided. This will trigger an email to be sent to the address, containing a magic link that will allow them to set a new password and authenticate.

If you have connected your deeplink handler with `StytchB2BClient`, the resulting magic link should be detected by your application and automatically parsed (via the `StytchB2BClient.handle()` method). See the instructions in the [top-level README](/README.md) for information on handling deeplink intents. You should get a `DeeplinkHandledStatus.ManualHandlingRequired` result, which includes the necessary token for resetting the member's password. If you are not using our deeplink handler, you must parse out the token from the deeplink yourself.

Once you have a password reset token, and have collected the member's new desired password, you can call the `StytchB2BClient.passwords.resetByEmail()` method to finish resetting their password.

Call the `StytchB2BClient.passwords.resetByExisting` method to reset a member's password by providing their existing password. Reset the member’s password and authenticate them. This endpoint checks that the existing password matches the stored value. The provided password needs to meet our password strength requirements, which can be checked in advance with the password strength endpoint. If the password and accompanying parameters are accepted, the password is securely stored for future authentication and the member is authenticated.

Call the `StytchB2BClient.passwords.resetBySession` method to reset a member's password using their currently authenticated session. Reset the member’s password and authenticate them. This endpoint checks that the session is valid and hasn’t expired or been revoked. The provided password needs to meet our password strength requirements, which can be checked in advance with the password strength endpoint. If the password and accompanying parameters are accepted, the password is securely stored for future authentication and the member is authenticated.

Call the `StytchB2BClient.passwords.strengthCheck()` method to check whether or not the member’s provided password is valid, and to provide feedback to the member on how to increase the strength of their password.