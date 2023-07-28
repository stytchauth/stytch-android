package com.stytch.sdk.ui

import androidx.activity.ComponentActivity
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.data.StytchStyles
import com.stytch.sdk.ui.data.StytchUIConfig

@Suppress("LongParameterList")
public class StytchUI private constructor(
    activity: ComponentActivity?,
    isB2B: Boolean?,
    styles: StytchStyles?,
    productConfig: StytchProductConfig?,
    onAuthenticated: ((StytchResult<*>) -> Unit)?,
) {
    private val activity: ComponentActivity
    private val isB2B: Boolean
    private val styles: StytchStyles
    private val productConfig: StytchProductConfig
    private val authHandler: StytchAuthHandler
    internal val bootstrapData: BootstrapData
        get() = if (this.isB2B) StytchB2BClient.bootstrapData else StytchClient.bootstrapData

    init {
        require(activity != null) { "Missing required activity" }
        require(onAuthenticated != null) { "Missing required authentication result handler" }
        this.activity = activity
        this.isB2B = isB2B ?: false
        this.styles = styles ?: StytchStyles()
        this.productConfig = productConfig ?: StytchProductConfig()
        this.authHandler = StytchAuthHandler(activity, onAuthenticated)
    }

    public fun authenticate() {
        authHandler.authenticate(
            StytchUIConfig(
                productConfig = this.productConfig,
                styles = this.styles,
                bootstrapData = this.bootstrapData,
            )
        )
    }

    public fun getStyles(): StytchStyles = this.styles

    public class Builder {
        private var activity: ComponentActivity? = null
        private var isB2B: Boolean? = false
        private var onAuthenticated: ((StytchResult<*>) -> Unit)? = null
        private var styles: StytchStyles? = null
        private var productConfig: StytchProductConfig? = null

        public fun activity(activity: ComponentActivity): Builder = apply {
            this.activity = activity
        }

        public fun isB2B(isB2B: Boolean): Builder = apply {
            this.isB2B = isB2B
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
            activity,
            isB2B,
            styles,
            productConfig,
            onAuthenticated,
        )
    }
}
