package com.stytch.sdk

internal class StytchConfig private constructor(
    val projectID: String,
    val secret: String,
    val deepLinkScheme: String,
    val deepLinkHost: String,
    val verifyEmail: Boolean,
    var uiCustomization: StytchUICustomization
) {


    public class Builder {

        private var deepLinkScheme: String = "app"
        private var deepLinkHost: String = "stytch.com"
        private var UICustomization: StytchUICustomization = StytchUICustomization()
        private var verifyEmail: Boolean = false
        private var projectID: String = ""
        private var secret: String = ""

        fun withDeepLinkScheme(scheme: String): Builder {
            deepLinkScheme = scheme
            return this
        }

        fun withDeepLinkHost(host: String): Builder {
            deepLinkHost = host
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
                deepLinkHost,
                verifyEmail,
                UICustomization
            )
        }


    }
}