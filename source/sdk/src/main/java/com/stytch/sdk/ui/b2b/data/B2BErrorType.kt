package com.stytch.sdk.ui.b2b.data

import androidx.annotation.StringRes
import com.stytch.sdk.R

internal enum class B2BErrorType(
    @StringRes val id: Int,
) {
    Default(R.string.stytch_b2b_default_error_message),
    EmailMagicLink(R.string.stytch_b2b_eml_error_message),
    Organization(R.string.stytch_b2b_organization_error_message),
    CannotJoinOrgDueToAuthPolicy(R.string.stytch_b2b_cannot_join_due_to_auth_policy),
    NoAuthenticationMethodsFound(R.string.stytch_b2B_no_auth_methods_found),
}
