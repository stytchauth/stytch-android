package com.stytch.sdk.consumer.userManagement

import android.os.Parcelable
import androidx.annotation.Keep
import com.stytch.sdk.consumer.network.models.UserData
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Keep
public sealed interface StytchUser : Parcelable {
    @Parcelize
    public data object Unavailable : StytchUser

    @Parcelize
    public data class Available(
        public val lastValidatedAt: Date,
        public val userData: UserData,
    ) : StytchUser
}
