package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.common.errors.StytchError
import kotlinx.parcelize.Parcelize

@Parcelize
public sealed class AuthenticationResult : Parcelable {
    public data object Authenticated : AuthenticationResult()

    @JacocoExcludeGenerated
    public data class Error(
        public val error: StytchError,
    ) : AuthenticationResult()
}
