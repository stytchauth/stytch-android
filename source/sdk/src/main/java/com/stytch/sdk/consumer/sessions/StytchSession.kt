package com.stytch.sdk.consumer.sessions

import android.os.Parcelable
import androidx.annotation.Keep
import com.stytch.sdk.consumer.network.models.SessionData
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Keep
public sealed interface StytchSession : Parcelable {
    @Parcelize
    public data object Unavailable : StytchSession

    @Parcelize
    public data class Available(
        public val lastValidatedAt: Date,
        public val sessionData: SessionData,
    ) : StytchSession
}
