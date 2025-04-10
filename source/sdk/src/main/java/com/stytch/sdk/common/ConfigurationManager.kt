package com.stytch.sdk.common

import android.app.Application
import android.content.Context
import com.stytch.sdk.common.dfp.ActivityProvider
import com.stytch.sdk.common.dfp.CaptchaProviderImpl
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.dfp.DFPProviderImpl
import com.stytch.sdk.common.extensions.getDeviceInfo
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.common.network.models.DFPProtectedAuthMode
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
    private var options: StytchClientOptions = StytchClientOptions()
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
        this.dfpProvider =
            DFPProviderImpl(
                publicToken = publicToken,
                dfppaDomain = options.endpointOptions.dfppaDomain,
                activityProvider = ActivityProvider(context.applicationContext as Application),
            )
        this.smsRetriever =
            StytchSMSRetrieverImpl(context) { code, sessionDurationMinutes ->
                smsRetriever.finish()
                client.smsAutofillCallback(code, sessionDurationMinutes)
            }
        client.commonApi.configure(publicToken, deviceInfo, client::getSessionToken)
        val bootstrapJob = refreshBootstrapAndApi(true)
        val sessionRehydrationJob = client.rehydrateSession()
        externalScope.launch(dispatchers.io) {
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
        // TODO: Align on naming with Nidal
        println("ConfigurationAnalyticEvent_${event.step} = ${event.duration}")
        // client.logEvent("ConfigurationAnalyticEvent_${event.step}", mapOf("duration" to event.duration), null)
    }

    private fun refreshBootstrapAndApi(reportTiming: Boolean = false): Job =
        externalScope.launch(dispatchers.io) {
            val start = Date().time
            applicationContext.get()?.let {
                refreshBootstrapData()
                client.commonApi.configureDFP(
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
