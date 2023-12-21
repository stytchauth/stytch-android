package com.stytch.sdk.ui.data

import com.stytch.sdk.common.network.models.BootstrapData

internal val DEFAULT_STYTCH_UI_CONFIG = StytchUIConfig(
    productConfig = StytchProductConfig(),
    styles = StytchStyles(),
    bootstrapData = BootstrapData(),
    publicToken = "",
)

internal val REALISTIC_STYTCH_UI_CONFIG = StytchUIConfig(
    productConfig = StytchProductConfig(
        products = listOf(
            StytchProduct.OAUTH,
            StytchProduct.EMAIL_MAGIC_LINKS,
            StytchProduct.OTP,
            StytchProduct.PASSWORDS,
        ),
        emailMagicLinksOptions = EmailMagicLinksOptions(),
        passwordOptions = PasswordOptions(),
        googleOauthOptions = GoogleOAuthOptions(),
        oAuthOptions = OAuthOptions(
            providers = listOf(OAuthProvider.GOOGLE, OAuthProvider.APPLE, OAuthProvider.GITHUB)
        ),
        otpOptions = OTPOptions(
            methods = listOf(OTPMethods.SMS, OTPMethods.WHATSAPP),
        )
    ),
    styles = StytchStyles(),
    bootstrapData = BootstrapData(disableSDKWatermark = true),
    publicToken = ""
)
