package com.stytch.sdk.b2b.sessions

import android.os.Parcelable
import androidx.annotation.Keep
import com.stytch.sdk.b2b.network.models.B2BSessionData
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Keep
public sealed interface StytchMemberSession : Parcelable {
    @Parcelize
    public data object Unavailable : StytchMemberSession

    @Parcelize
    public data class Available(
        public val lastValidatedAt: Date,
        public val memberSessionData: B2BSessionData,
    ) : StytchMemberSession
}
