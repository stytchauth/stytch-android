package com.stytch.sdk.common.network

import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.dfp.CaptchaProvider
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.common.network.models.DFPProtectedAuthMode

internal interface CommonApi {
    fun configure(
        publicToken: String,
        deviceInfo: DeviceInfo,
        getSessionToken: () -> String?,
    )

    fun configureDFP(
        dfpProvider: DFPProvider,
        captchaProvider: CaptchaProvider,
        dfpProtectedAuthEnabled: Boolean,
        dfpProtectedAuthMode: DFPProtectedAuthMode,
    )

    suspend fun getBootstrapData(): StytchResult<BootstrapData>
}
