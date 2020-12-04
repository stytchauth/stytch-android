package com.stytch.sdk

internal class StytchConfig private constructor(
    val projectID: String,
    val secret: String,
    val deepLinkScheme: String,
    val verifyEmail: Boolean,
    var uiCustomization: StytchUICustomization
) {

    val deepLinkHost: String
        get() = when (Stytch.instance.environment) {
            StytchEnvironment.LIVE -> "api.stytch.com"
            StytchEnvironment.TEST -> "test.stytch.com"
        }

    public class Builder {

        private var deepLinkScheme: String = "app"
        private var UICustomization: StytchUICustomization = StytchUICustomization()
        private var verifyEmail: Boolean = false
        private var projectID: String = ""
        private var secret: String = ""

        fun withDeepLinkScheme(scheme: String): Builder {
            deepLinkScheme = scheme
            return this
        }

        fun withCustomization(UICustomization: StytchUICustomization): Builder {
            this.UICustomization = UICustomization
            return this
        }

        fun withVerifyEmail(verifyEmail: Boolean): Builder {
            this.verifyEmail = verifyEmail
            return this
        }

        fun withAuth(projectID: String, secret: String): Builder {
            this.projectID = projectID
            this.secret = secret
            return this
        }


        fun build(): StytchConfig {
            return StytchConfig(
                projectID,
                secret,
                deepLinkScheme,
                verifyEmail,
                UICustomization
            )
        }


    }
}