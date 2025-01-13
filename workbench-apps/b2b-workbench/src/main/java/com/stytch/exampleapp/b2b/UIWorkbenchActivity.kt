package com.stytch.exampleapp.b2b

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.sessions.B2BSessions
import com.stytch.sdk.ui.b2b.StytchB2BUI
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.AuthenticationResult
import com.stytch.sdk.ui.b2b.data.B2BOAuthOptions
import com.stytch.sdk.ui.b2b.data.B2BOAuthProviderConfig
import com.stytch.sdk.ui.b2b.data.B2BOAuthProviders
import com.stytch.sdk.ui.b2b.data.StytchB2BProduct
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig

class UIWorkbenchActivity : ComponentActivity() {
    private val defaultConfig =
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
    private val noOrgFound =
        StytchB2BUI
            .Builder()
            .apply {
                activity(this@UIWorkbenchActivity)
                productConfig(
                    defaultConfig.copy(
                        organizationSlug = "not-a-real-slug",
                        authFlowType = AuthFlowType.ORGANIZATION,
                    ),
                )
                onAuthenticated(::onAuthentication)
            }.build()
    private val noMfaOrgConfig =
        StytchB2BUI
            .Builder()
            .apply {
                activity(this@UIWorkbenchActivity)
                productConfig(
                    defaultConfig.copy(
                        organizationSlug = "no-mfa",
                        authFlowType = AuthFlowType.ORGANIZATION,
                    ),
                )
                onAuthenticated(::onAuthentication)
            }.build()
    private val noMfaWithEOTP =
        StytchB2BUI
            .Builder()
            .apply {
                activity(this@UIWorkbenchActivity)
                productConfig(
                    defaultConfig.copy(
                        organizationSlug = "no-mfa",
                        authFlowType = AuthFlowType.ORGANIZATION,
                        products = listOf(StytchB2BProduct.EMAIL_OTP),
                    ),
                )
                onAuthenticated(::onAuthentication)
            }.build()
    private val noMfaWithEOTPAndEML =
        StytchB2BUI
            .Builder()
            .apply {
                activity(this@UIWorkbenchActivity)
                productConfig(
                    defaultConfig.copy(
                        organizationSlug = "no-mfa",
                        authFlowType = AuthFlowType.ORGANIZATION,
                        products = listOf(StytchB2BProduct.EMAIL_OTP, StytchB2BProduct.EMAIL_MAGIC_LINKS),
                    ),
                )
                onAuthenticated(::onAuthentication)
            }.build()
    private val noMfaWithEOTPAndEMLAndPasswords =
        StytchB2BUI
            .Builder()
            .apply {
                activity(this@UIWorkbenchActivity)
                productConfig(
                    defaultConfig.copy(
                        organizationSlug = "no-mfa",
                        authFlowType = AuthFlowType.ORGANIZATION,
                        products =
                            listOf(
                                StytchB2BProduct.EMAIL_OTP,
                                StytchB2BProduct.EMAIL_MAGIC_LINKS,
                                StytchB2BProduct.PASSWORDS,
                            ),
                    ),
                )
                onAuthenticated(::onAuthentication)
            }.build()
    private val noEMLNoMFA =
        StytchB2BUI
            .Builder()
            .apply {
                activity(this@UIWorkbenchActivity)
                productConfig(
                    defaultConfig.copy(
                        products = defaultConfig.products.filterNot { it == StytchB2BProduct.EMAIL_MAGIC_LINKS },
                        organizationSlug = "no-mfa",
                        authFlowType = AuthFlowType.ORGANIZATION,
                    ),
                )
                onAuthenticated(::onAuthentication)
            }.build()
    private val enforcedMfaOrgConfig =
        StytchB2BUI
            .Builder()
            .apply {
                activity(this@UIWorkbenchActivity)
                productConfig(
                    defaultConfig.copy(
                        organizationSlug = "enforced-mfa",
                        authFlowType = AuthFlowType.ORGANIZATION,
                    ),
                )
                onAuthenticated(::onAuthentication)
            }.build()

    private val enforcedMfaWithEOTP =
        StytchB2BUI
            .Builder()
            .apply {
                activity(this@UIWorkbenchActivity)
                productConfig(
                    defaultConfig.copy(
                        organizationSlug = "enforced-mfa",
                        authFlowType = AuthFlowType.ORGANIZATION,
                        products = listOf(StytchB2BProduct.EMAIL_OTP),
                    ),
                )
                onAuthenticated(::onAuthentication)
            }.build()
    private val discoveryConfig =
        StytchB2BUI
            .Builder()
            .apply {
                activity(this@UIWorkbenchActivity)
                productConfig(
                    defaultConfig.copy(
                        authFlowType = AuthFlowType.DISCOVERY,
                    ),
                )
                onAuthenticated(::onAuthentication)
            }.build()
    private val discoveryConfigWithEOTP =
        StytchB2BUI
            .Builder()
            .apply {
                activity(this@UIWorkbenchActivity)
                productConfig(
                    defaultConfig.copy(
                        authFlowType = AuthFlowType.DISCOVERY,
                        products = listOf(StytchB2BProduct.EMAIL_OTP),
                    ),
                )
                onAuthenticated(::onAuthentication)
            }.build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Button(onClick = noOrgFound::authenticate) {
                    Text("Launch Org Not Found Authentication")
                }
                Button(onClick = noMfaOrgConfig::authenticate) {
                    Text("Launch NoMFA Org-Specific Authentication")
                }
                Button(onClick = noEMLNoMFA::authenticate) {
                    Text("Launch NoMFA with No EML Org-Specific Authentication")
                }
                Button(onClick = noMfaWithEOTP::authenticate) {
                    Text("Launch NoMFA with EOTP Org-Specific Authentication")
                }
                Button(onClick = noMfaWithEOTPAndEML::authenticate) {
                    Text("Launch NoMFA with EML + EOTP Org-Specific Authentication")
                }
                Button(onClick = noMfaWithEOTPAndEMLAndPasswords::authenticate) {
                    Text("Launch NoMFA with EML + EOTP + Passwords Org-Specific Authentication")
                }
                Button(onClick = enforcedMfaOrgConfig::authenticate) {
                    Text("Launch Enforced-Mfa Org-Specific Authentication")
                }
                Button(onClick = enforcedMfaWithEOTP::authenticate) {
                    Text("Launch Enforced-Mfa With EOTP Org-Specific Authentication")
                }
                Button(onClick = discoveryConfig::authenticate) {
                    Text("Launch Discovery Flow Authentication")
                }
                Button(onClick = discoveryConfigWithEOTP::authenticate) {
                    Text("Launch Discovery Flow With EOTP Authentication")
                }
                Button(onClick = { StytchB2BClient.sessions.revoke(B2BSessions.RevokeParams(true)) {} }) {
                    Text("Log out")
                }
            }
        }
    }

    private fun onAuthentication(result: AuthenticationResult) {
        when (result) {
            is AuthenticationResult.Authenticated -> {
                Toast
                    .makeText(
                        this@UIWorkbenchActivity,
                        "Authentication Succeeded",
                        Toast.LENGTH_LONG,
                    ).show()
            }
            is AuthenticationResult.Error -> {
                Toast.makeText(this@UIWorkbenchActivity, result.error.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
