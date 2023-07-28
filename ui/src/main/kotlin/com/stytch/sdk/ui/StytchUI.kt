package com.stytch.sdk.ui

import androidx.activity.ComponentActivity
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.data.StytchStyles
import com.stytch.sdk.ui.data.StytchUIConfig

@Suppress("LongParameterList")
public class StytchUI private constructor(
    activity: ComponentActivity?,
    publicToken: String?,
    styles: StytchStyles?,
    productConfig: StytchProductConfig?,
    onAuthenticated: ((StytchResult<*>) -> Unit)?,
) {
    private val activity: ComponentActivity
    private val styles: StytchStyles
    private val productConfig: StytchProductConfig
    private val authHandler: StytchAuthHandler
    private val publicToken: String

    internal val bootstrapData: BootstrapData
        get() = StytchClient.bootstrapData

    init {
        require(activity != null) { "Missing required activity" }
        require(publicToken != null) { "Missing required public token" }
        require(onAuthenticated != null) { "Missing required authentication result handler" }
        this.activity = activity
        this.styles = styles ?: StytchStyles()
        this.productConfig = productConfig ?: StytchProductConfig()
        this.authHandler = StytchAuthHandler(activity, onAuthenticated)
        this.publicToken = publicToken
    }

    public fun authenticate() {
        authHandler.authenticate(
            StytchUIConfig(
                productConfig = this.productConfig,
                styles = this.styles,
                bootstrapData = this.bootstrapData,
                publicToken = this.publicToken,
            )
        )
    }

    public fun getStyles(): StytchStyles = this.styles

    public class Builder {
        private var activity: ComponentActivity? = null
        private var onAuthenticated: ((StytchResult<*>) -> Unit)? = null
        private var styles: StytchStyles? = null
        private var productConfig: StytchProductConfig? = null
        private var publicToken: String? = null

        public fun activity(activity: ComponentActivity): Builder = apply {
            this.activity = activity
        }

        public fun publicToken(publicToken: String): Builder = apply {
            this.publicToken = publicToken
        }

        public fun onAuthenticated(onAuthenticated: (StytchResult<*>) -> Unit): Builder = apply {
            this.onAuthenticated = onAuthenticated
        }

        public fun styles(styles: StytchStyles): Builder = apply {
            this.styles = styles
        }

        public fun productConfig(config: StytchProductConfig): Builder = apply {
            this.productConfig = config
        }

        public fun build(): StytchUI = StytchUI(
            activity = activity,
            publicToken = publicToken,
            styles = styles,
            productConfig = productConfig,
            onAuthenticated = onAuthenticated,
        )
    }
}
