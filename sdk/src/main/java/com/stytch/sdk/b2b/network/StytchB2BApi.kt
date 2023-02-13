package com.stytch.sdk.b2b.network

import androidx.annotation.VisibleForTesting
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.BasicData
import com.stytch.sdk.common.network.CommonRequests
import com.stytch.sdk.common.network.StytchAuthHeaderInterceptor
import com.stytch.sdk.common.network.StytchDataResponse
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
            suspend fun loginOrCreate(
                email: String,
                organizationId: String,
                loginRedirectUrl: String?,
                signupRedirectUrl: String?,
                codeChallenge: String,
                loginTemplateId: String?,
                signupTemplateId: String?,
            ): StytchResult<BasicData> = safeB2BApiCall {
                apiService.loginOrCreateUserByEmail(
                    B2BRequests.MagicLinks.Email.LoginOrCreateUserRequest(
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

    internal object Organizations {
        suspend fun getOrganization(): StytchResult<Organization> = safeB2BApiCall {
            apiService.getOrganization()
        }

        suspend fun getMember(): StytchResult<MemberData> = safeB2BApiCall {
            apiService.getMember()
        }
    }
}
