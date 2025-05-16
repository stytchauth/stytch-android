package com.stytch.sdk.ui.b2b.data

import androidx.annotation.StringRes
import com.stytch.sdk.R

internal enum class B2BErrorType(
    @StringRes val id: Int,
) {
    Default(R.string.stytch_b2b_error_default),
    EmailMagicLink(R.string.stytch_b2b_error_eml),
    Organization(R.string.stytch_b2b_error_organization),
    CannotJoinOrgDueToAuthPolicy(R.string.stytch_b2b_error_auth_policy),
    NoAuthenticationMethodsFound(R.string.stytch_b2b_error_product_configuration),
}
