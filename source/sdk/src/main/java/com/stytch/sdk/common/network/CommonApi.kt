package com.stytch.sdk.common.network

import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.dfp.DFPConfiguration
import com.stytch.sdk.common.network.models.BootstrapData

internal interface CommonApi {
    fun configure(
        publicToken: String,
        deviceInfo: DeviceInfo,
        getSessionToken: () -> String?,
        dfpConfiguration: DFPConfiguration,
    )

    fun configureDFP(dfpConfiguration: DFPConfiguration)

    suspend fun getBootstrapData(): StytchResult<BootstrapData>
}
