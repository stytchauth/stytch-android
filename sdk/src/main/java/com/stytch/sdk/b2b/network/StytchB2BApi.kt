package com.stytch.sdk.b2b.network

import androidx.annotation.VisibleForTesting
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.B2BAuthData
import com.stytch.sdk.b2b.network.models.B2BEMLAuthenticateData
import com.stytch.sdk.b2b.network.models.B2BRequests
import com.stytch.sdk.b2b.network.models.EmailResetResponseData
import com.stytch.sdk.b2b.network.models.IB2BAuthData
import com.stytch.sdk.b2b.network.models.MemberResponseData
import com.stytch.sdk.b2b.network.models.OrganizationResponseData
import com.stytch.sdk.b2b.network.models.PasswordsAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.SessionResetResponseData
import com.stytch.sdk.b2b.network.models.StrengthCheckResponseData
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.StytchAuthHeaderInterceptor
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.common.network.safeApiCall
import java.lang.RuntimeException

internal object StytchB2BApi {
    internal lateinit var publicToken: String
    private lateinit var deviceInfo: DeviceInfo

    // save reference for changing auth header
    // make sure api is configured before accessing this variable
    @Suppress("MaxLineLength")
    @VisibleForTesting
    internal val authHeaderInterceptor: StytchAuthHeaderInterceptor by lazy {
        if (!isInitialized) {
            throw StytchExceptions.Critical(
                RuntimeException(
                    "StytchB2BApi not configured. You must call 'configure(...)' before using any functionality of the " // ktlint-disable max-line-length
                )
            )
        }
        StytchAuthHeaderInterceptor(
            deviceInfo,
            publicToken,
        ) { StytchB2BClient.sessionStorage.sessionToken }
    }

    internal fun configure(publicToken: String, deviceInfo: DeviceInfo) {
        this.publicToken = publicToken
        this.deviceInfo = deviceInfo
    }

    internal val isInitialized: Boolean
        get() {
            return ::publicToken.isInitialized && ::deviceInfo.isInitialized
        }

    @VisibleForTesting
    internal val apiService: StytchB2BApiService by lazy {
        StytchB2BClient.assertInitialized()
        ApiService.createApiService(
            Constants.WEB_URL,
            authHeaderInterceptor,
            { StytchB2BClient.sessionStorage.revoke() },
            StytchB2BApiService::class.java
        )
    }

    internal suspend fun <T1, T : StytchDataResponse<T1>> safeB2BApiCall(
        apiCall: suspend () -> T
    ): StytchResult<T1> = safeApiCall({ StytchB2BClient.assertInitialized() }) {
        apiCall()
    }

    internal object MagicLinks {
        object Email {
            @Suppress("LongParameterList")
            suspend fun loginOrSignupByEmail(
                email: String,
                organizationId: String,
                loginRedirectUrl: String?,
                signupRedirectUrl: String?,
                codeChallenge: String,
                loginTemplateId: String?,
                signupTemplateId: String?,
            ): StytchResult<BasicData> = safeB2BApiCall {
                apiService.loginOrSignupByEmail(
                    B2BRequests.MagicLinks.Email.LoginOrSignupRequest(
                        email = email,
                        organizationId = organizationId,
                        loginRedirectUrl = loginRedirectUrl,
                        signupRedirectUrl = signupRedirectUrl,
                        codeChallenge = codeChallenge,
                        loginTemplateId = loginTemplateId,
                        signupTemplateId = signupTemplateId,
                    )
                )
            }

            suspend fun authenticate(
                token: String,
                sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
                codeVerifier: String
            ): StytchResult<B2BEMLAuthenticateData> = safeB2BApiCall {
                apiService.authenticate(
                    B2BRequests.MagicLinks.AuthenticateRequest(
                        token = token,
                        codeVerifier = codeVerifier,
                        sessionDurationMinutes = sessionDurationMinutes.toInt()
                    )
                )
            }
        }
    }

    internal object Sessions {
        suspend fun authenticate(
            sessionDurationMinutes: UInt? = null
        ): StytchResult<IB2BAuthData> = safeB2BApiCall {
            apiService.authenticateSessions(
                CommonRequests.Sessions.AuthenticateRequest(
                    sessionDurationMinutes = sessionDurationMinutes?.toInt()
                )
            )
        }

        suspend fun revoke(): StytchResult<BasicData> = safeB2BApiCall {
            apiService.revokeSessions()
        }
    }

    internal object Organization {
        suspend fun getOrganization(): StytchResult<OrganizationResponseData> = safeB2BApiCall {
            apiService.getOrganization()
        }
    }

    internal object Member {
        suspend fun getMember(): StytchResult<MemberResponseData> = safeB2BApiCall {
            apiService.getMember()
        }
    }

    internal object Passwords {
        suspend fun authenticate(
            organizationId: String,
            emailAddress: String,
            password: String,
            sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
        ): StytchResult<PasswordsAuthenticateResponseData> = safeB2BApiCall {
            apiService.authenticatePassword(
                B2BRequests.Passwords.AuthenticateRequest(
                    organizationId = organizationId,
                    emailAddress = emailAddress,
                    password = password,
                    sessionDurationMinutes = sessionDurationMinutes.toInt(),
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun resetByEmailStart(
            organizationId: String,
            emailAddress: String,
            loginRedirectUrl: String?,
            resetPasswordRedirectUrl: String?,
            resetPasswordExpirationMinutes: UInt?,
            resetPasswordTemplateId: String?,
            codeChallenge: String,
        ): StytchResult<BasicData> = safeB2BApiCall {
            apiService.resetPasswordByEmailStart(
                B2BRequests.Passwords.ResetByEmailStartRequest(
                    organizationId = organizationId,
                    emailAddress = emailAddress,
                    loginRedirectUrl = loginRedirectUrl,
                    resetPasswordRedirectUrl = resetPasswordRedirectUrl,
                    resetPasswordExpirationMinutes = resetPasswordExpirationMinutes?.toInt(),
                    resetPasswordTemplateId = resetPasswordTemplateId,
                    codeChallenge = codeChallenge,
                )
            )
        }

        suspend fun resetByEmail(
            passwordResetToken: String,
            password: String,
            sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
            codeVerifier: String,
        ): StytchResult<EmailResetResponseData> = safeB2BApiCall {
            apiService.resetPasswordByEmail(
                B2BRequests.Passwords.ResetByEmailRequest(
                    passwordResetToken = passwordResetToken,
                    password = password,
                    sessionDurationMinutes = sessionDurationMinutes.toInt(),
                    codeVerifier = codeVerifier
                )
            )
        }

        suspend fun resetByExisting(
            organizationId: String,
            emailAddress: String,
            existingPassword: String,
            newPassword: String,
            sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
        ): StytchResult<B2BAuthData> = safeB2BApiCall {
            apiService.resetPasswordByExisting(
                B2BRequests.Passwords.ResetByExistingPasswordRequest(
                    organizationId = organizationId,
                    emailAddress = emailAddress,
                    existingPassword = existingPassword,
                    newPassword = newPassword,
                    sessionDurationMinutes = sessionDurationMinutes.toInt(),
                )
            )
        }

        suspend fun resetBySession(
            organizationId: String,
            password: String,
        ): StytchResult<SessionResetResponseData> = safeB2BApiCall {
            apiService.resetPasswordBySession(
                B2BRequests.Passwords.ResetBySessionRequest(
                    organizationId = organizationId,
                    password = password,
                )
            )
        }

        suspend fun strengthCheck(
            email: String?,
            password: String,
        ): StytchResult<StrengthCheckResponseData> = safeB2BApiCall {
            apiService.passwordStrengthCheck(
                B2BRequests.Passwords.StrengthCheckRequest(
                    email = email,
                    password = password,
                )
            )
        }
    }
}
