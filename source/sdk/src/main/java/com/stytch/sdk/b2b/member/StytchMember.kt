package com.stytch.sdk.b2b.member

import android.os.Parcelable
import androidx.annotation.Keep
import com.stytch.sdk.b2b.network.models.MemberData
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Keep
public sealed interface StytchMember : Parcelable {
    @Parcelize
    public data object Unavailable : StytchMember

    @Parcelize
    public data class Available(
        public val lastValidatedAt: Date,
        public val memberData: MemberData,
    ) : StytchMember
}
