package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.ui.shared.data.SessionOptions
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
@JacocoExcludeGenerated
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
        val allowCreateOrganization: Boolean = true,
        val directCreateOrganizationForNoMembership: Boolean = false,
        val mfaProductOrder: List<MfaMethod> = emptyList(),
        val mfaProductInclude: List<MfaMethod> =
            listOf(
                MfaMethod.SMS,
                MfaMethod.TOTP,
            ),
        val locale: Locale? = Locale.EN,
    ) : Parcelable
