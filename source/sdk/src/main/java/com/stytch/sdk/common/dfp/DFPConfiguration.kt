package com.stytch.sdk.common.dfp

import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.common.network.models.DFPProtectedAuthMode

@JacocoExcludeGenerated
internal data class DFPConfiguration(
    val dfpProvider: DFPProvider,
    val captchaProvider: CaptchaProvider,
    val dfpProtectedAuthEnabled: Boolean,
    val dfpProtectedAuthMode: DFPProtectedAuthMode,
)
