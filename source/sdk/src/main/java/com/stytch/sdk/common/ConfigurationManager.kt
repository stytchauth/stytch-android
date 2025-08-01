package com.stytch.sdk.common

import android.app.Application
import android.content.Context
import com.stytch.sdk.common.dfp.CaptchaProviderImpl
import com.stytch.sdk.common.dfp.DFPConfiguration
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.dfp.DFPProviderImpl
import com.stytch.sdk.common.dfp.DFPType
import com.stytch.sdk.common.dfp.WebviewActivityProvider
import com.stytch.sdk.common.extensions.getDeviceInfo
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.common.network.models.DFPProtectedAuthMode
import com.stytch.sdk.common.network.models.Vertical
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.pkcePairManager.PKCEPairManagerImpl
import com.stytch.sdk.common.smsRetriever.StytchSMSRetriever
import com.stytch.sdk.common.smsRetriever.StytchSMSRetrieverImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.Date
import java.util.UUID

internal class ConfigurationManager {
    internal lateinit var deviceInfo: DeviceInfo
    internal lateinit var appSessionId: String
    internal var options: StytchClientOptions = StytchClientOptions()
    internal var applicationContext = WeakReference<Context>(null)
    internal var dispatchers: StytchDispatchers = StytchDispatchers()
    internal var externalScope: CoroutineScope = CoroutineScope(SupervisorJob())
    internal var pkcePairManager: PKCEPairManager = PKCEPairManagerImpl(StorageHelper, EncryptionManager)
    internal lateinit var dfpProvider: DFPProvider
    internal var bootstrapData: BootstrapData = BootstrapData()
    internal lateinit var publicToken: String
    internal lateinit var smsRetriever: StytchSMSRetriever
    internal var isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var configurationStartTime = 0L
    private lateinit var client: StytchClientCommon

    fun configure(
        client: StytchClientCommon,
        context: Context,
        publicToken: String,
        options: StytchClientOptions = StytchClientOptions(),
        storageHelperInitializationJob: Job,
    ) {
        if (isAlreadyConfiguredFor(publicToken, options)) {
            return
        }
        configurationStartTime = Date().time
        this.client = client
        this.publicToken = publicToken
        this.options = options
        this.applicationContext = WeakReference(context.applicationContext)
        this.deviceInfo = context.getDeviceInfo()
        this.appSessionId = "app-session-id-${UUID.randomUUID()}"
        val activityProvider =
            if (options.dfpType == DFPType.Webview) {
                WebviewActivityProvider(context.applicationContext as Application)
            } else {
                null
            }
        this.dfpProvider =
            DFPProviderImpl(
                scope = externalScope,
                context = context.applicationContext,
                publicToken = publicToken,
                dfppaDomain = options.endpointOptions.dfppaDomain,
                dfpType = options.dfpType,
                activityProvider = activityProvider,
            )
        this.smsRetriever =
            StytchSMSRetrieverImpl(context) { code, sessionDurationMinutes ->
                smsRetriever.finish()
                client.smsAutofillCallback(code, sessionDurationMinutes)
            }
        val dfpConfiguration =
            DFPConfiguration(
                dfpProvider = dfpProvider,
                captchaProvider =
                    CaptchaProviderImpl(
                        context.applicationContext as Application,
                        externalScope,
                        bootstrapData.captchaSettings.siteKey,
                    ),
                dfpProtectedAuthEnabled = bootstrapData.dfpProtectedAuthEnabled,
                dfpProtectedAuthMode = bootstrapData.dfpProtectedAuthMode ?: DFPProtectedAuthMode.OBSERVATION,
            )
        client.commonApi.configure(
            publicToken,
            deviceInfo,
            options.endpointOptions,
            client::getSessionToken,
            dfpConfiguration,
        )
        externalScope.launch(dispatchers.io) {
            storageHelperInitializationJob.join()
            val bootstrapJob = refreshBootstrapAndApi(true)
            val sessionRehydrationJob = client.rehydrateSession()
            listOf(bootstrapJob, sessionRehydrationJob).joinAll()
            client.logEvent("client_initialization_success", null, null)
            isInitialized.value = true
            emitAnalyticsEvent(
                ConfigurationAnalyticsEvent(
                    step = ConfigurationStep.IS_INITIALIZED,
                    duration = Date().time - configurationStartTime,
                ),
            )
            client.onFinishedInitialization()
        }
        NetworkChangeListener.configure(context.applicationContext, ::refreshBootstrapAndApi)
        AppLifecycleListener.configure(::refreshBootstrapAndApi)
    }

    private fun isAlreadyConfiguredFor(
        publicToken: String,
        options: StytchClientOptions,
    ) = this::publicToken.isInitialized && this.publicToken == publicToken && this.options == options

    fun emitAnalyticsEvent(event: ConfigurationAnalyticsEvent) {
        // TODO: Align on naming with iOS
        // println("ConfigurationAnalyticEvent_${event.step} = ${event.duration}")
        // client.logEvent("ConfigurationAnalyticEvent_${event.step}", mapOf("duration" to event.duration), null)
    }

    private fun refreshBootstrapAndApi(reportTiming: Boolean = false): Job =
        externalScope.launch(dispatchers.io) {
            val start = Date().time
            applicationContext.get()?.let {
                refreshBootstrapData()
                val dfpConfiguration =
                    DFPConfiguration(
                        dfpProvider = dfpProvider,
                        captchaProvider =
                            CaptchaProviderImpl(
                                it as Application,
                                externalScope,
                                bootstrapData.captchaSettings.siteKey,
                            ),
                        dfpProtectedAuthEnabled = bootstrapData.dfpProtectedAuthEnabled,
                        dfpProtectedAuthMode = bootstrapData.dfpProtectedAuthMode ?: DFPProtectedAuthMode.OBSERVATION,
                    )
                client.commonApi.configureDFP(dfpConfiguration)
                if (reportTiming) {
                    emitAnalyticsEvent(
                        ConfigurationAnalyticsEvent(
                            step = ConfigurationStep.BOOTSTRAPPING,
                            duration = Date().time - start,
                        ),
                    )
                }
            }
        }

    private var isRefreshingBootstrap = false

    suspend fun refreshBootstrapData() {
        if (isRefreshingBootstrap) return
        isRefreshingBootstrap = true
        bootstrapData =
            when (val res = client.commonApi.getBootstrapData()) {
                is StytchResult.Success -> res.value
                else -> bootstrapData
            }
        when {
            client.expectedVertical == Vertical.CONSUMER && bootstrapData.vertical == Vertical.B2B -> {
                StytchLog.e(
                    "This application is using a Stytch client for Consumer projects, but the public token is for a " +
                        "Stytch B2B project. Use a B2B Stytch client instead, or verify that the public token is " +
                        "correct.",
                )
            }
            client.expectedVertical == Vertical.B2B && bootstrapData.vertical == Vertical.CONSUMER -> {
                StytchLog.e(
                    "This application is using a Stytch client for B2B projects, but the public token is for a " +
                        "Stytch Consumer project. Use a Consumer Stytch client instead, or verify that the public " +
                        "token is correct.",
                )
            }
        }
        isRefreshingBootstrap = false
    }
}

internal data class ConfigurationAnalyticsEvent(
    val step: ConfigurationStep,
    val duration: Long,
)

internal enum class ConfigurationStep {
    BOOTSTRAPPING,
    SESSION_HYDRATION,
    IS_INITIALIZED,
}
