# StytchUI Usage
The UI SDK automatically handles all necessary OAuth, Email Magic Link, and Password Reset deeplinks. To enable this functionality, you need to add a specific redirect URL in your [Stytch Dashboard](https://stytch.com/dashboard/redirect-urls): `stytchui-[YOUR_PUBLIC_TOKEN]://deeplink`, and set it as valid for Signups, Logins, and Password Resets.

The Stytch UI uses the Builder pattern to initialize the UI, and allows you to fully customize the look, feel, and supported products.

Within your host activity, define the UI and any configuration options you would like. Examples for both the Consumer and B2B UI are below:

## Consumer UI
```kotlin
import com.stytch.sdk.ui.StytchUI

private val stytchUi = StytchUI.Builder().apply {
    activity(this@MyActivity)
    productConfig(
        StytchProductConfig(
            products = listOf(
                StytchProduct.OAUTH,
                StytchProduct.EMAIL_MAGIC_LINKS,
                StytchProduct.OTP,
                StytchProduct.PASSWORDS,
            ),
            // this is used for configuring Google OneTap specifically
            googleOauthOptions = GoogleOAuthOptions(
                clientId = BuildConfig.GOOGLE_OAUTH_CLIENT_ID
            ),
            oAuthOptions = OAuthOptions(
                loginRedirectURL = "uiworkbench://oauth", // This should match what you defined in your manifestPlaceholders
                signupRedirectURL = "uiworkbench://oauth", // This should match what you defined in your manifestPlaceholders
                providers = listOf(OAuthProvider.GOOGLE, OAuthProvider.APPLE, OAuthProvider.GITHUB)
            ),
            otpOptions = OTPOptions(
                methods = listOf(OTPMethods.SMS, OTPMethods.WHATSAPP),
            )
        )
    )
    // this handler is what is called when an authentication event (success, failure, or cancellation) occurs
    onAuthenticated {
        when (it) {
            is StytchResult.Success -> {
                Toast.makeText(this@MyActivity, "Authentication Succeeded", Toast.LENGTH_LONG).show()
            }
            is StytchResult.Error -> {
                Toast.makeText(this@MyActivity, it.exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}.build()
```

## B2B UI
```kotlin
import com.stytch.sdk.ui.StytchUI

private val stytchUi = 
    StytchB2BUI
        .Builder()
        .apply {
            activity(this@UIWorkbenchActivity)
            productConfig(
                StytchB2BProductConfig(
                    products =
                        listOf(
                            StytchB2BProduct.EMAIL_MAGIC_LINKS,
                            StytchB2BProduct.OAUTH,
                            StytchB2BProduct.PASSWORDS,
                            StytchB2BProduct.SSO,
                        ),
                    oauthOptions =
                        B2BOAuthOptions(
                            providers =
                                listOf(
                                    B2BOAuthProviderConfig(type = B2BOAuthProviders.GOOGLE),
                                    B2BOAuthProviderConfig(type = B2BOAuthProviders.GITHUB),
                                ),
                        ),
                )
            )
            onAuthenticated {
                when (it) {
                    is AuthenticationResult.Authenticated -> {
                        Toast.makeText(this@MyActivity, "Authentication Succeeded", Toast.LENGTH_LONG).show()
                    }
                    is AuthenticationResult.Error -> {
                        Toast.makeText(this@MyActivity, it.exception.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }.build()
```


Then, no matter which version you are using, simply call the `authenticate` method from your UI to launch the Stytch UI:
```kotlin
Button(modifier = Modifier.fillMaxSize(), onClick = stytchUi::authenticate) {
   Text("Launch Authentication")
}
```

The result of the authentication flow, either successful or not, will be reported to the `onAuthenticated` handler that you specified when building the StytchUI instance.
