# Passwords
Stytch offers a broad suite of passwordless authentication products, but for businesses and applications that depend on passwords, or are looking for a bridge between password-based and passwordless authentication, this SDK provides a fully featured Passwords product.

The Passwords client enables you to verify that a password meets your configured strength check policy; create new password-based users; authenticate users with an email and password; and reset passwords with an existing password, authenticated session, or by email.

## Implementation

### Strength Check
This method allows you to check whether or not the user’s provided password is valid, and to provide feedback to the user on how to increase the strength of their password.

This endpoint adapts to your Project's password strength configuration. If you're using  [zxcvbn](https://stytch.com/docs/guides/passwords/strength-policy), the default, your passwords are considered valid if the strength score is >= 3. If you're using  [LUDS](https://stytch.com/docs/guides/passwords/strength-policy), your passwords are considered valid if they meet the requirements that you've set with Stytch. You may update your password strength configuration in the  [stytch dashboard](https://stytch.com/dashboard/password-strength-config).

All password flows are subject to your configured strength check policy, so it's good practice to always perform a strength check before attempting to create or reset a user's password.

```kotlin
val strengthCheckResponse = StytchClient.passwords.strengthCheck(  
    Passwords.StrengthCheckParameters(  
        email = "user@email.com",  
        password = "my user password"  
    )  
)  
when (strengthCheckResponse) {  
    is StytchResult.Success -> {  
        // use the properties of strengthCheckResponse.value to
        // determine if a password is valid
    }
    is StytchResult.Error -> {  
        // something went wrong  
    }
}
```

### Create
This method creates a new user with a password, and can optionally create a new session for this user. 

When creating new Passwords users, it's good practice to enforce an email verification flow. We'd recommend checking out our  [Email verification guide](https://stytch.com/docs/guides/passwords/email-verification/overview)  for more information.

```kotlin
val createResponse = StytchClient.passwords.create(  
    Passwords.CreateParameters(  
        email = "user@email.com",  
        password = "my user password"  
    )  
)  
when (createResponse) {  
    is StytchResult.Success -> {  
        // The user was created successfully. If you passed in a session duration
        // in the parameters, a session has also been minted for them
    }
    is StytchResult.Error -> {  
        // something went wrong  
    }
}
```

### Authenticate
This method is used for authenticating a user with an email address and password, and verifies that the user has a password currently set, and that the entered password is correct. There are two instances where the endpoint will return a  reset_password  error even if they enter their previous password:

1.  The user’s credentials appeared in the HaveIBeenPwned dataset. We force a password reset to ensure that the user is the legitimate owner of the email address, and not a malicious actor abusing the compromised credentials.
2.   A user that has previously authenticated with email/password uses a passwordless authentication method tied to the same email address (e.g. Magic Links, Google OAuth) for the first time. Any subsequent email/password authentication attempt will result in this error. We force a password reset in this instance in order to safely deduplicate the account by email address, without introducing the risk of a pre-hijack account takeover attack.

```kotlin
val authResponse = StytchClient.passwords.authenticate(  
    Passwords.AuthParameters(  
        email = "user@email.com",  
        password = "my user password"  
    )  
)  
when (authResponse) {  
    is StytchResult.Success -> {  
        // The user was logged in successfully
    }
    is StytchResult.Error -> {  
        // something went wrong
    }
}
```

### Reset
From time to time, you may need to reset a user's password. The Stytch Android SDK provides three flows for doing so, depending on the user's current state and needs.

#### By Existing Password
If a user already knows their existing password, for instance if they just want to change their password for some reason, you can use the `StytchClient.passwords.resetByExistingPassword()` method:

```kotlin
val resetResponse = StytchClient.passwords.resetByExistingPassword(  
    Passwords.ResetByExistingPasswordParameters(  
        email = "user@email.com",
        existingPassword = "my current password",
        newPassword = "my new password",
    )  
)  
when (resetResponse) {  
    is StytchResult.Success -> {  
        // The user's password was successfully changed
    }
    is StytchResult.Error -> {  
        // something went wrong
    }
}
```

#### By Current Session
If a user is logged in, for instance with an Email Magic Link, but does not know their password, they can use their existing session to authenticate this request. The endpoint will error if the session does not have a password, email magic link, or email OTP authentication factor that has been issued within the last 5 minutes.

```kotlin
val resetResponse = StytchClient.passwords.resetBySession(  
    Passwords.ResetBySessionParameters(  
        password = "my new password",
    )  
)  
when (resetResponse) {  
    is StytchResult.Success -> {  
        // The user's password was successfully changed
    }
    is StytchResult.Error -> {  
        // something went wrong
    }
}
```

#### By Email
Lastly, a user may not be authenticated by any method or know their current password. In this scenario, they must reset their password by confirming their email address. This is a two part flow, that is similar to the [Email Magic Link](./EmailMagicLink.md) flow, and requires that you have configured [deeplinking](./Deeplinks.md) for your application.

First, you will start the resetByEmail flow:
```kotlin
val startResponse = StytchClient.passwords.resetByEmailStart(
    Passwords.ResetByEmailStartParameters(  
        email = "user@email.com",  
    )
)
when (startResponse) {  
    is StytchResult.Success -> {
        // The password reset email was successfuly sent
    }
    is StytchResult.Error -> {
        // something went wrong
    }
}
```

Next, you will handle the deeplink, and authenticate the token. This endpoint checks that the magic link token is valid, hasn’t expired, or already been used. If the token and password are accepted, the password is securely stored for future authentication and the user is authenticated.
```kotlin
fun handleDeeplink(uri: URI) {
    val tokenType = uri.getQueryParameter("stytch_token_type") ?:return
    val token = uri.getQueryParameter("token") ?: return
    viewModelScope.launch {
        if (tokenType == "reset_password") {
            val finishResponse = StytchClient.passwords.resetByEmail(
                Passwords.ResetByEmailParameters(
                    token = token,
                    password = "my new password"
                )
            )
            when (finishResponse) {  
                is StytchResult.Success -> {  
                    // The token was consumed and the user's password has been changed
                }
                is StytchResult.Error -> {  
                    // something went wrong
                }
            }
        }
    }
}
```

## Further Reading
For more information on Stytch's Password product, check out the [guide](https://stytch.com/docs/guides/passwords/api).