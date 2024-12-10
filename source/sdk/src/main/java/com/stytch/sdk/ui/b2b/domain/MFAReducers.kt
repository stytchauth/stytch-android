package com.stytch.sdk.ui.b2b.domain

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.State
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import com.stytch.sdk.b2b.network.models.IB2BAuthDataWithMFA
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.MFAPrimaryInfoState
import com.stytch.sdk.ui.b2b.data.MFASMSState
import com.stytch.sdk.ui.b2b.extensions.getAuthMethodsForMember
import com.stytch.sdk.ui.b2b.extensions.getDefaultMFAMethod
import com.stytch.sdk.ui.b2b.extensions.getEnrolledMfaMethods
import com.stytch.sdk.ui.b2b.extensions.getOrganizationMfaOptionsSupported
import com.stytch.sdk.ui.b2b.extensions.supportsMethod
import com.stytch.sdk.ui.b2b.extensions.toInternalOrganizationData
import com.stytch.sdk.ui.b2b.navigation.Route
import com.stytch.sdk.ui.b2b.navigation.Routes
import java.util.Date

internal const val DEFAULT_SMS_OTP_EXPIRATION_MS = 1000 * 60 * 2; // 2 minutes

private fun getSmsOtpCodeExpiration(): Date = Date(Date().time + DEFAULT_SMS_OTP_EXPIRATION_MS)

internal fun handleResponseThatNeedsAdditionalAuthentication(
    state: State<B2BUIState>,
    response: IB2BAuthDataWithMFA,
): ChangedState<B2BUIState> =
    if (response.memberSession != null) {
        handleUserIsFullyAuthenticated(state)
    } else if (response.primaryRequired != null) {
        handleAdditionalPrimaryAuthRequired(state, response)
    } else {
        handleMFARequired(state, response)
    }

// If the member is fully authenticated, there's no need to continue
private fun handleUserIsFullyAuthenticated(state: State<B2BUIState>): ChangedState<B2BUIState> =
    state.mutate {
        copy(currentRoute = postAuthScreen)
    }

// Additional primary auth may be required
private fun handleAdditionalPrimaryAuthRequired(
    state: State<B2BUIState>,
    response: IB2BAuthDataWithMFA,
): ChangedState<B2BUIState> {
    val primaryAuthMethods = response.getAuthMethodsForMember()
    return state.mutate {
        val newState =
            copy(
                authFlowType = AuthFlowType.ORGANIZATION,
                activeOrganization = response.organization.toInternalOrganizationData(),
                emailState =
                    emailState.copy(
                        emailAddress = response.member.email,
                        validEmail = true,
                        emailVerified = response.member.emailAddressVerified,
                    ),
                phoneNumberState =
                    phoneNumberState.copy(
                        countryCode = response.member.mfaPhoneNumber?.extractCountryCode() ?: "1",
                        phoneNumber = response.member.mfaPhoneNumber?.extractPhoneNumber() ?: "",
                    ),
                currentRoute = Routes.Main,
                primaryAuthMethods = primaryAuthMethods,
                mfaPrimaryInfoState =
                    MFAPrimaryInfoState(
                        enrolledMfaMethods = emptyList(),
                        memberId = response.memberId,
                        memberPhoneNumber = response.member.mfaPhoneNumber,
                        organizationId = response.organization.organizationId,
                        organizationMfaOptionsSupported = emptyList(),
                    ),
            )
        if (primaryAuthMethods.isEmpty()) {
            return@mutate newState.copy(
                currentRoute = Routes.Error,
                b2BErrorType = B2BErrorType.CannotJoinOrgDueToAuthPolicy,
            )
        }
        return@mutate newState
    }
}

private fun handleMFARequired(
    state: State<B2BUIState>,
    response: IB2BAuthDataWithMFA,
): ChangedState<B2BUIState> {
    val enrolledMfaMethods = response.getEnrolledMfaMethods()
    val defaultMfaMethod = response.getDefaultMFAMethod()
    val smsImplicitlySent = response.mfaRequired?.secondaryAuthInitiated == "sms_otp"
    val mfaPrimaryInfoState =
        MFAPrimaryInfoState(
            enrolledMfaMethods = enrolledMfaMethods,
            memberId = response.memberId,
            memberPhoneNumber = response.member.mfaPhoneNumber,
            organizationId = response.organization.organizationId,
            organizationMfaOptionsSupported = response.getOrganizationMfaOptionsSupported(),
        )
    val entryMethod: MfaMethod? =
        if (smsImplicitlySent) {
            MfaMethod.SMS
        } else {
            defaultMfaMethod ?: enrolledMfaMethods.find {
                response.getOrganizationMfaOptionsSupported().supportsMethod(it)
            }
        }
    return entryMethod?.let {
        handleUserIsEnrolledInMfa(state, smsImplicitlySent, mfaPrimaryInfoState, it)
    } ?: handleUserMustEnrollInMfa(state, mfaPrimaryInfoState)
}

// Use the member's preferred method, or any suitable fallback if the
// preferred method is not available for whatever reason. However, if an
// SMS was automatically sent, we should proceed directly to SMS OTP
// entry (even if the member's phone number is not yet verified).
private fun handleUserIsEnrolledInMfa(
    state: State<B2BUIState>,
    smsImplicitlySent: Boolean,
    mfaPrimaryInfoState: MFAPrimaryInfoState,
    entryMethod: MfaMethod,
): ChangedState<B2BUIState> {
    return state.mutate {
        return@mutate when (entryMethod) {
            MfaMethod.SMS -> {
                copy(
                    mfaPrimaryInfoState = mfaPrimaryInfoState,
                    mfaSMSState =
                        MFASMSState(
                            codeExpiration =
                                if (smsImplicitlySent) {
                                    getSmsOtpCodeExpiration()
                                } else {
                                    null
                                },
                        ),
                    currentRoute = Routes.SMSOTPEntry,
                    phoneNumberState =
                        phoneNumberState.copy(
                            countryCode = mfaPrimaryInfoState.memberPhoneNumber?.extractCountryCode() ?: "1",
                            phoneNumber = mfaPrimaryInfoState.memberPhoneNumber?.extractPhoneNumber() ?: "",
                        ),
                )
            }
            MfaMethod.TOTP -> {
                copy(
                    mfaPrimaryInfoState = mfaPrimaryInfoState,
                    currentRoute = Routes.TOTPEntry,
                )
            }
            MfaMethod.NONE -> {
                // We should never get here, but just in case, we'll send them to enrollment selection
                copy(
                    mfaPrimaryInfoState = mfaPrimaryInfoState,
                    currentRoute = Routes.MFAEnrollmentSelection,
                )
            }
        }
    }
}

// If the member is not enrolled in MFA, we need to enroll them. Note that
// it's possible the member is actually enrolled, but using a method we
// don't recognize. Unfortunately, we can't distinguish between these
// cases; we'll offer to enroll the user, but enrollment will likely fail
// if the member can't complete MFA first.
private fun handleUserMustEnrollInMfa(
    state: State<B2BUIState>,
    mfaPrimaryInfoState: MFAPrimaryInfoState,
): ChangedState<B2BUIState> =
    state.mutate {
        copy(
            mfaPrimaryInfoState = mfaPrimaryInfoState,
            currentRoute = getNextEnrollmentScreen(mfaPrimaryInfoState, uiIncludedMfaMethods),
        )
    }

internal val ALL_MFA_METHODS = setOf(MfaMethod.TOTP, MfaMethod.SMS)

internal fun getEnabledMethods(
    orgSupportedMethods: List<MfaMethod>,
    uiIncludedMfaMethods: List<MfaMethod>,
): Set<MfaMethod> {
    // If the org only supported a restricted set of methods, use that
    if (orgSupportedMethods.isNotEmpty()) {
        return orgSupportedMethods.toSet()
    }
    // Use the configured list of included methods, or all methods by default
    return if (uiIncludedMfaMethods.isNotEmpty()) {
        uiIncludedMfaMethods.toSet()
    } else {
        ALL_MFA_METHODS
    }
}

private fun getNextEnrollmentScreen(
    mfaPrimaryInfoState: MFAPrimaryInfoState,
    uiIncludedMfaMethods: List<MfaMethod>,
): Route {
    val enabledMethods =
        getEnabledMethods(
            mfaPrimaryInfoState.organizationMfaOptionsSupported,
            uiIncludedMfaMethods,
        )
    var nextRoute: Route? = null
    if (enabledMethods.size == 1) {
        if (enabledMethods.contains(MfaMethod.SMS)) {
            nextRoute = Routes.SMSOTPEnrollment
        } else if (enabledMethods.contains(MfaMethod.TOTP)) {
            nextRoute = Routes.TOTPEnrollment
        }
    }
    return nextRoute ?: Routes.MFAEnrollmentSelection
}

private fun String.parsePhoneNumber(): PhoneNumber? =
    try {
        PhoneNumberUtil.getInstance().parse(this, null)
    } catch (_: NumberParseException) {
        null
    }

private fun String.extractCountryCode(): String? = parsePhoneNumber()?.countryCode?.toString()

private fun String.extractPhoneNumber(): String? = parsePhoneNumber()?.nationalNumber?.toString()
