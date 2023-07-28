package com.stytch.sdk.ui

import androidx.activity.ComponentActivity
import androidx.annotation.VisibleForTesting
import com.stytch.sdk.common.StytchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("LongParameterList")
public class StytchUI private constructor(
    activity: ComponentActivity?,
    publicToken: String?,
    isB2B: Boolean?,
    styles: StytchStyles?,
    productConfig: StytchProductConfig?,
    onAuthenticated: ((StytchResult<*>) -> Unit)?,
    scope: CoroutineScope
) {
    private val activity: ComponentActivity
    private val publicToken: String
    private val isB2B: Boolean
    private val styles: StytchStyles
    private val productConfig: StytchProductConfig
    private val authHandler: StytchAuthHandler
    private lateinit var sdkConfig: SDKConfig
    private var isReady: Boolean = false

    init {
        require(!publicToken.isNullOrBlank()) { "Missing required public token" }
        require(activity != null) { "Missing required activity" }
        require(onAuthenticated != null) { "Missing required authentication result handler" }
        require(productConfig != null) { "Missing required productConfig" }
        this.activity = activity
        this.publicToken = publicToken
        this.isB2B = isB2B ?: false
        this.styles = styles ?: StytchStyles()
        this.productConfig = productConfig
        this.authHandler = StytchAuthHandler(activity, onAuthenticated)
        scope.launch {
            sdkConfig = fetchSDKConfig()
            isReady = true
        }
    }

    private suspend fun fetchSDKConfig(): SDKConfig = withContext(Dispatchers.IO) {
        SDKConfig()
    }

    public fun authenticate() {
        authHandler.authenticate(productConfig)
    }

    public class Builder {
        private var activity: ComponentActivity? = null
        private var publicToken: String? = null
        private var isB2B: Boolean? = false
        private var onAuthenticated: ((StytchResult<*>) -> Unit)? = null
        private var styles: StytchStyles? = null
        private var productConfig: StytchProductConfig? = null

        @OptIn(DelicateCoroutinesApi::class)
        private var scope: CoroutineScope = GlobalScope

        public fun activity(activity: ComponentActivity): Builder = apply {
            this.activity = activity
        }

        public fun publicToken(publicToken: String): Builder = apply {
            this.publicToken = publicToken
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

        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal fun scope(scope: CoroutineScope): Builder = apply {
            this.scope = scope
        }

        public fun build(): StytchUI = StytchUI(
            activity,
            publicToken,
            isB2B,
            styles,
            productConfig,
            onAuthenticated,
            scope
        )
    }
}
