package com.stytch.sdk.ui.shared.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.stytch.sdk.R
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchAPIErrorType
import com.stytch.sdk.common.errors.StytchError
import java.util.regex.Pattern

private val EMAIL_ADDRESS_PATTERN =
    Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+",
    )

internal fun String.isValidEmailAddress(): Boolean = EMAIL_ADDRESS_PATTERN.matcher(this).matches()

@Composable
internal fun String.mapZxcvbnToStringResource(): String =
    when (this) {
        "Use a few words, avoid common phrases." -> stringResource(R.string.stytch_zxcvbn_suggestion_1)
        "No need for symbols, digits, or uppercase letters." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_2,
            )
        "Add another word or two. Uncommon words are better." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_3,
            )
        "Use a longer keyboard pattern with more turns." -> stringResource(R.string.stytch_zxcvbn_suggestion_4)
        "Avoid repeated words and characters." -> stringResource(R.string.stytch_zxcvbn_suggestion_5)
        "Avoid sequences." -> stringResource(R.string.stytch_zxcvbn_suggestion_6)
        "Avoid recent years." -> stringResource(R.string.stytch_zxcvbn_suggestion_7)
        "Avoid years that are associated with you." -> stringResource(R.string.stytch_zxcvbn_suggestion_8)
        "Avoid dates and years that are associated with you." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_9,
            )
        "Capitalization doesn't help very much." -> stringResource(R.string.stytch_zxcvbn_suggestion_10)
        "All-uppercase is almost as easy to guess as all-lowercase." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_11,
            )
        "Reversed words aren't much harder to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_12)
        "Predictable substitutions like '@' instead of 'a' don't help very much." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_13,
            )
        "Short keyboard patterns are easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_14)
        "Straight rows of keys are easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_15)
        "Repeats like \"abcabcabc\" are only slightly harder to guess than \"abc\"." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_16,
            )
        "Repeats like \"aaa\" are easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_17)
        "Sequences like \"abc\" or \"6543\" are easy to guess." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_18,
            )
        "Recent years are easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_19)
        "Dates are often easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_20)
        "This is a top-10 common password." -> stringResource(R.string.stytch_zxcvbn_suggestion_21)
        "This is a top-100 common password." -> stringResource(R.string.stytch_zxcvbn_suggestion_22)
        "This is a very common password." -> stringResource(R.string.stytch_zxcvbn_suggestion_23)
        "This is similar to a commonly used password." -> stringResource(R.string.stytch_zxcvbn_suggestion_24)
        "A word by itself is easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_25)
        "Names and surnames by themselves are easy to guess." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_26,
            )
        "Common names and surnames are easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_27)
        else -> this
    }

internal fun StytchError.getUserFacingErrorMessageId(): Int? =
    if (this is StytchAPIError) {
        when (errorType) {
            StytchAPIErrorType.UNAUTHORIZED_CREDENTIALS -> R.string.stytch_error_unauthorized_credentials
            StytchAPIErrorType.USER_UNAUTHENTICATED -> R.string.stytch_error_user_unauthenticated
            StytchAPIErrorType.EMAIL_NOT_FOUND -> R.string.stytch_error_email_not_found
            StytchAPIErrorType.OTP_CODE_NOT_FOUND -> R.string.stytch_error_otp_code_not_found
            StytchAPIErrorType.BREACHED_PASSWORD -> R.string.stytch_error_breached_password
            StytchAPIErrorType.NO_USER_PASSWORD -> R.string.stytch_error_no_user_password
            StytchAPIErrorType.INVALID_CODE -> R.string.stytch_error_invalid_code
            StytchAPIErrorType.TOO_MANY_REQUESTS -> R.string.stytch_error_too_many_requests
            StytchAPIErrorType.SESSION_NOT_FOUND -> R.string.stytch_error_session_not_found
            StytchAPIErrorType.USER_LOCK_LIMIT_REACHED -> R.string.stytch_error_user_lock_limit_reached
            StytchAPIErrorType.RESET_PASSWORD -> R.string.stytch_error_reset_password
            StytchAPIErrorType.UNABLE_TO_AUTH_OTP_CODE -> R.string.stytch_error_unable_to_auth_otp_code
            StytchAPIErrorType.NO_ACTIVE_BIOMETRIC_REGISTRATIONS ->
                R.string.stytch_error_no_active_biometric_registrations
            StytchAPIErrorType.UNABLE_TO_AUTH_MAGIC_LINK -> R.string.stytch_error_unable_to_auth_magic_link
            StytchAPIErrorType.PHONE_NUMBER_NOT_FOUND -> R.string.stytch_error_phone_number_not_found
            StytchAPIErrorType.INVALID_PHONE_NUMBER_COUNTRY_CODE ->
                R.string.stytch_error_invalid_phone_number_country_code
            StytchAPIErrorType.SESSION_TOO_OLD_TO_RESET_PASSWORD ->
                R.string.stytch_error_session_too_old_to_reset_password
            StytchAPIErrorType.INVALID_EMAIL -> R.string.stytch_error_invalid_email
            StytchAPIErrorType.UNAUTHORIZED_ACTION -> R.string.stytch_error_unauthorized_action
            StytchAPIErrorType.WEAK_PASSWORD -> R.string.stytch_error_weak_password
            StytchAPIErrorType.DUPLICATE_EMAIL -> R.string.stytch_error_duplicate_email
            StytchAPIErrorType.INVALID_PHONE_NUMBER -> R.string.stytch_error_invalid_phone_number
            StytchAPIErrorType.OAUTH_AUTH_CODE_ERROR -> R.string.stytch_error_oauth_auth_code_error
            StytchAPIErrorType.OAUTH_FLOW_CALLBACK_ERROR -> R.string.stytch_error_oauth_flow_callback_error
            StytchAPIErrorType.OAUTH_TOKEN_NOT_FOUND -> R.string.stytch_error_oauth_token_not_found
            StytchAPIErrorType.PKCE_MISMATCH -> R.string.stytch_error_pkce_mismatch
            StytchAPIErrorType.AD_BLOCKER_DETECTED -> R.string.stytch_error_ad_blocker_detected
            StytchAPIErrorType.STALE_FACTORS -> R.string.stytch_error_stale_factors
            StytchAPIErrorType.INTERNAL_SERVER_ERROR -> R.string.stytch_error_internal_server_error
            StytchAPIErrorType.INVALID_METHOD_ID -> R.string.stytch_error_invalid_method_id
            StytchAPIErrorType.UNABLE_TO_AUTH_BIOMETRIC_REGISTRATION ->
                R.string.stytch_error_unable_to_auth_biometric_registration
            StytchAPIErrorType.UNSUBSCRIBED_PHONE_NUMBER -> R.string.stytch_error_unsubscribed_phone_number
            StytchAPIErrorType.EMAIL_TEMPLATE_NOT_FOUND -> R.string.stytch_error_email_template_not_found
            StytchAPIErrorType.PKCE_EXPECTED_CODE_VERIFIER -> R.string.stytch_error_pkce_expected_code_verifier
            StytchAPIErrorType.CAPTCHA_REQUIRED -> R.string.stytch_error_captcha_required
            StytchAPIErrorType.INACTIVE_EMAIL -> R.string.stytch_error_inactive_email
            StytchAPIErrorType.MEMBER_PASSWORD_NOT_FOUND -> R.string.stytch_error_member_password_not_found
            StytchAPIErrorType.SERVER_UNAVAILABLE -> R.string.stytch_error_server_unavailable
            StytchAPIErrorType.TOO_MANY_BIOMETRIC_REGISTRATIONS_FOR_USER ->
                R.string.stytch_error_too_many_biometric_registrations_for_user
            StytchAPIErrorType.DUPLICATE_PHONE_NUMBER -> R.string.stytch_error_duplicate_phone_number
            StytchAPIErrorType.OAUTH_INVALID_CALLBACK_REQUEST -> R.string.stytch_error_oauth_invalid_callback_request
            StytchAPIErrorType.INTERMEDIATE_SESSION_NOT_FOUND -> R.string.stytch_error_intermediate_session_not_found
            StytchAPIErrorType.NO_MATCH_FOR_PROVIDED_MAGIC_LINK_URL ->
                R.string.stytch_error_no_match_for_provided_magic_link_url
            StytchAPIErrorType.TOTP_CODE_ALREADY_AUTHENTICATED -> R.string.stytch_error_totp_code_already_authenticated
            StytchAPIErrorType.INVALID_SESSION_DURATION_MINUTES ->
                R.string.stytch_error_invalid_session_duration_minutes
            StytchAPIErrorType.INVALID_CONSUMER_ENDPOINT -> R.string.stytch_error_invalid_consumer_endpoint
            StytchAPIErrorType.CROSS_ORG_PASSWORDS_NOT_ENABLED -> R.string.stytch_error_cross_org_passwords_not_enabled
            StytchAPIErrorType.INVALID_SESSION_DURATION -> R.string.stytch_error_invalid_session_duration
            StytchAPIErrorType.INVALID_LOCALE -> R.string.stytch_error_invalid_locale
            StytchAPIErrorType.MAGIC_LINK_NOT_FOUND -> R.string.stytch_error_magic_link_not_found
            else -> null
        }
    } else {
        null
    }
