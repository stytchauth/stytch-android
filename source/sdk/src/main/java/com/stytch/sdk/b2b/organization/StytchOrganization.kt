package com.stytch.sdk.b2b.organization

import android.os.Parcelable
import androidx.annotation.Keep
import com.stytch.sdk.b2b.network.models.OrganizationData
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Keep
public sealed interface StytchOrganization : Parcelable {
    @Parcelize
    public data object Unavailable : StytchOrganization

    @Parcelize
    public data class Available(
        public val lastValidatedAt: Date,
        public val organizationData: OrganizationData,
    ) : StytchOrganization
}
