package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import com.stytch.sdk.ui.shared.data.SessionOptions
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
public data class StytchB2BProductConfig
    @JvmOverloads
    constructor(
        val products: List<StytchB2BProduct> =
            listOf(
                StytchB2BProduct.EMAIL_MAGIC_LINKS,
                StytchB2BProduct.PASSWORDS,
                StytchB2BProduct.OAUTH,
            ),
        val authFlowType: AuthFlowType = AuthFlowType.DISCOVERY,
        val organizationSlug: String? = null,
        val sessionOptions: SessionOptions = SessionOptions(),
        val emailMagicLinksOptions: B2BEmailMagicLinkOptions = B2BEmailMagicLinkOptions(),
        val passwordOptions: B2BPasswordOptions = B2BPasswordOptions(),
        val oauthOptions: B2BOAuthOptions = B2BOAuthOptions(),
        val directLoginForSingleMembership: DirectLoginForSingleMembershipOptions? = null,
        val disableCreateOrganization: Boolean = false,
        val mfaProductOrder: List<StytchB2BMFAProduct> = emptyList(),
        val mfaProductInclude: List<StytchB2BMFAProduct> =
            listOf(
                StytchB2BMFAProduct.SMS,
                StytchB2BMFAProduct.TOTP,
            ),
    ) : Parcelable
