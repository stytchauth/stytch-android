package com.stytch.sdk.ui.b2b

import androidx.activity.ComponentActivity
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.data.StytchB2BUIConfig
import com.stytch.sdk.ui.shared.data.StytchStyles

/**
 * This class configures the Stytch UI component. You use this class to specify all of the necessary configuration
 * parameters.
 *
 * It uses the Builder pattern, so you will not be directly constructing an instance of this class.
 */
@Suppress("LongParameterList")
public class StytchB2BUI private constructor(
    activity: ComponentActivity?,
    styles: StytchStyles?,
    productConfig: StytchB2BProductConfig?,
    onAuthenticated: ((StytchResult<*>) -> Unit)?,
) {
    private val activity: ComponentActivity
    private val styles: StytchStyles
    private val productConfig: StytchB2BProductConfig
    private val authHandler: StytchB2BAuthHandler

    internal val bootstrapData: BootstrapData
        get() = StytchB2BClient.bootstrapData

    init {
        require(activity != null) { "Missing required activity" }
        require(onAuthenticated != null) { "Missing required authentication result handler" }
        this.activity = activity
        this.styles = styles ?: StytchStyles()
        this.productConfig = productConfig ?: StytchB2BProductConfig()
        this.authHandler = StytchB2BAuthHandler(activity, onAuthenticated)
    }

    /**
     * Call this method from your application to begin the authentication flow. It will create and launch the UI
     * activity and configure the listeners for the result
     */
    public fun authenticate() {
        authHandler.authenticate(
            StytchB2BUIConfig(
                productConfig = this.productConfig,
                styles = this.styles,
                bootstrapData = this.bootstrapData,
            ),
        )
    }

    /**
     * The Builder class is used to construct an instance of the StytchUI class
     */
    public class Builder {
        private var activity: ComponentActivity? = null
        private var onAuthenticated: ((StytchResult<*>) -> Unit)? = null
        private var styles: StytchStyles? = null
        private var productConfig: StytchB2BProductConfig? = null

        /**
         * Sets the hosting activity that the Stytch UI activity will report results back to
         * @param activity Your host activity from which you will launch the Stytch UI activity and listen for results
         */
        public fun activity(activity: ComponentActivity): Builder =
            apply {
                this.activity = activity
            }

        /**
         * Sets the handler that will be notified of authenticate results
         * @param onAuthenticated the handler that will be notified
         */
        public fun onAuthenticated(onAuthenticated: (StytchResult<*>) -> Unit): Builder =
            apply {
                this.onAuthenticated = onAuthenticated
            }

        /**
         * Sets the styles used by the UI. This is optional, and if not provided, default Stytch Styles will be applied,
         * providing both dark and light themes
         * @param styles a [StytchStyles] instance
         */
        public fun styles(styles: StytchStyles): Builder =
            apply {
                this.styles = styles
            }

        /**
         * Sets the product configuration, including which products are supported and their associated configurations
         * @param config a [StytchB2BProductConfig] instance
         */
        public fun productConfig(config: StytchB2BProductConfig): Builder =
            apply {
                this.productConfig = config
            }

        /**
         * Builds an instance of the StytchUI class
         */
        public fun build(): StytchB2BUI =
            StytchB2BUI(
                activity = activity,
                styles = styles,
                productConfig = productConfig,
                onAuthenticated = onAuthenticated,
            )
    }
}
