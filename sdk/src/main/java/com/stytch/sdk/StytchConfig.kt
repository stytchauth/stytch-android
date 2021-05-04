package com.stytch.sdk

import android.net.Uri

internal class StytchConfig private constructor(
    val projectID: String,
    val secret: String,
    val deepLinkScheme: String,
    val deepLinkHost: String,
    val verifyEmail: Boolean,
    val universalLink: String?,
    var uiCustomization: StytchUICustomization
) {

    public class Builder {

        private var deepLinkScheme: String = "app"
        private var deepLinkHost: String = "stytch.com"
        private var uiCustomization: StytchUICustomization = StytchUICustomization()
        private var verifyEmail: Boolean = false
        private var projectID: String = ""
        private var secret: String = ""
        private var universalLink: String? = null

        fun withDeepLinkScheme(scheme: String): Builder {
            deepLinkScheme = scheme
            return this
        }

        fun withDeepLinkHost(host: String): Builder {
            deepLinkHost = host
            return this
        }

        fun withUniversalLink(link: Uri): Builder {
            universalLink = link.path
            link.scheme?.let { deepLinkScheme = it }
            link.host?.let { deepLinkHost = it }
            return this
        }

        fun withCustomization(UICustomization: StytchUICustomization): Builder {
            this.uiCustomization = UICustomization
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
                    universalLink,
                    uiCustomization,
            )
        }
    }

}
