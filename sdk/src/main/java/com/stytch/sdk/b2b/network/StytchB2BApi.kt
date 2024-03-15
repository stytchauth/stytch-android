package com.stytch.sdk.b2b.network

import androidx.annotation.VisibleForTesting
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.AuthMethods
import com.stytch.sdk.b2b.network.models.B2BAuthData
import com.stytch.sdk.b2b.network.models.B2BEMLAuthenticateData
import com.stytch.sdk.b2b.network.models.B2BRequests
import com.stytch.sdk.b2b.network.models.DiscoveredOrganizationsResponseData
import com.stytch.sdk.b2b.network.models.DiscoveryAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.EmailInvites
import com.stytch.sdk.b2b.network.models.EmailJitProvisioning
import com.stytch.sdk.b2b.network.models.EmailResetResponseData
import com.stytch.sdk.b2b.network.models.IB2BAuthData
import com.stytch.sdk.b2b.network.models.IntermediateSessionExchangeResponseData
import com.stytch.sdk.b2b.network.models.MemberDeleteAuthenticationFactorData
import com.stytch.sdk.b2b.network.models.MemberResponseData
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.b2b.network.models.MfaMethods
import com.stytch.sdk.b2b.network.models.MfaPolicy
import com.stytch.sdk.b2b.network.models.OrganizationCreateResponseData
import com.stytch.sdk.b2b.network.models.OrganizationDeleteResponseData
import com.stytch.sdk.b2b.network.models.OrganizationMemberDeleteResponseData
import com.stytch.sdk.b2b.network.models.OrganizationResponseData
import com.stytch.sdk.b2b.network.models.OrganizationUpdateResponseData
import com.stytch.sdk.b2b.network.models.PasswordsAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.SSOAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.SessionExchangeResponseData
import com.stytch.sdk.b2b.network.models.SessionResetResponseData
import com.stytch.sdk.b2b.network.models.SsoJitProvisioning
import com.stytch.sdk.b2b.network.models.StrengthCheckResponseData
import com.stytch.sdk.b2b.network.models.UpdateMemberResponseData
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.NoResponseResponse
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.dfp.CaptchaProvider
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.errors.StytchSDKNotConfiguredError
import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.InfoHeaderModel
import com.stytch.sdk.common.network.StytchAuthHeaderInterceptor
import com.stytch.sdk.common.network.StytchDFPInterceptor
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.common.network.models.DFPProtectedAuthMode
import com.stytch.sdk.common.network.models.NoResponseData
import com.stytch.sdk.common.network.safeApiCall

internal object StytchB2BApi {
    internal lateinit var publicToken: String
    private lateinit var deviceInfo: DeviceInfo

    // save reference for changing auth header
    // make sure api is configured before accessing this variable
    @Suppress("MaxLineLength")
    @VisibleForTesting
    internal val authHeaderInterceptor: StytchAuthHeaderInterceptor by lazy {
        if (!isInitialized) {
            throw StytchSDKNotConfiguredError("StytchB2BClient")
        }
        StytchAuthHeaderInterceptor(
            deviceInfo,
            publicToken,
        ) { StytchB2BClient.sessionStorage.sessionToken }
    }

    internal fun configure(
        publicToken: String,
        deviceInfo: DeviceInfo,
    ) {
        this.publicToken = publicToken
        this.deviceInfo = deviceInfo
    }

    internal fun configureDFP(
        dfpProvider: DFPProvider,
        captchaProvider: CaptchaProvider,
        dfpProtectedAuthEnabled: Boolean,
        dfpProtectedAuthMode: DFPProtectedAuthMode,
    ) {
        StytchB2BClient.assertInitialized()
        dfpProtectedStytchApiService =
            ApiService.createApiService(
                Constants.WEB_URL,
                authHeaderInterceptor,
                StytchDFPInterceptor(dfpProvider, captchaProvider, dfpProtectedAuthEnabled, dfpProtectedAuthMode),
                { StytchB2BClient.sessionStorage.revoke() },
                StytchB2BApiService::class.java,
            )
    }

    internal val isInitialized: Boolean
        get() {
            return ::publicToken.isInitialized && ::deviceInfo.isInitialized
        }

    internal val isTestToken: Boolean
        get() {
            StytchB2BClient.assertInitialized()
            return publicToken.contains("public-token-test")
        }

    private val regularStytchApiService: StytchB2BApiService by lazy {
        ApiService.createApiService(
            Constants.WEB_URL,
            authHeaderInterceptor,
            null,
            { StytchB2BClient.sessionStorage.revoke() },
            StytchB2BApiService::class.java,
        )
    }

    private lateinit var dfpProtectedStytchApiService: StytchB2BApiService

    @VisibleForTesting
    internal val apiService: StytchB2BApiService
        get() {
            StytchB2BClient.assertInitialized()
            return if (::dfpProtectedStytchApiService.isInitialized) {
                dfpProtectedStytchApiService
            } else {
                regularStytchApiService
            }
        }

    internal suspend fun <T1, T : StytchDataResponse<T1>> safeB2BApiCall(apiCall: suspend () -> T): StytchResult<T1> =
        safeApiCall({ StytchB2BClient.assertInitialized() }) {
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
            ): StytchResult<BasicData> =
                safeB2BApiCall {
                    apiService.loginOrSignupByEmail(
                        B2BRequests.MagicLinks.Email.LoginOrSignupRequest(
                            email = email,
                            organizationId = organizationId,
                            loginRedirectUrl = loginRedirectUrl,
                            signupRedirectUrl = signupRedirectUrl,
                            codeChallenge = codeChallenge,
                            loginTemplateId = loginTemplateId,
                            signupTemplateId = signupTemplateId,
                        ),
                    )
                }

            suspend fun authenticate(
                token: String,
                sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
                codeVerifier: String,
            ): StytchResult<B2BEMLAuthenticateData> =
                safeB2BApiCall {
                    apiService.authenticate(
                        B2BRequests.MagicLinks.AuthenticateRequest(
                            token = token,
                            codeVerifier = codeVerifier,
                            sessionDurationMinutes = sessionDurationMinutes.toInt(),
                        ),
                    )
                }

            suspend fun invite(
                emailAddress: String,
                inviteRedirectUrl: String? = null,
                inviteTemplateId: String? = null,
                name: String? = null,
                untrustedMetadata: Map<String, Any?>? = null,
                locale: String? = null,
                roles: List<String>? = null,
            ): StytchResult<MemberResponseData> =
                safeB2BApiCall {
                    apiService.sendInviteMagicLink(
                        B2BRequests.MagicLinks.Invite.InviteRequest(
                            emailAddress = emailAddress,
                            inviteRedirectUrl = inviteRedirectUrl,
                            inviteTemplateId = inviteTemplateId,
                            name = name,
                            untrustedMetadata = untrustedMetadata,
                            locale = locale,
                            roles = roles,
                        ),
                    )
                }
        }

        object Discovery {
            suspend fun send(
                email: String,
                discoveryRedirectUrl: String?,
                codeChallenge: String,
                loginTemplateId: String?,
            ): StytchResult<BasicData> =
                safeB2BApiCall {
                    apiService.sendDiscoveryMagicLink(
                        B2BRequests.MagicLinks.Discovery.SendRequest(
                            email = email,
                            discoveryRedirectUrl = discoveryRedirectUrl,
                            codeChallenge = codeChallenge,
                            loginTemplateId = loginTemplateId,
                        ),
                    )
                }

            suspend fun authenticate(
                token: String,
                codeVerifier: String,
            ): StytchResult<DiscoveryAuthenticateResponseData> =
                safeB2BApiCall {
                    apiService.authenticateDiscoveryMagicLink(
                        B2BRequests.MagicLinks.Discovery.AuthenticateRequest(
                            token = token,
                            codeVerifier = codeVerifier,
                        ),
                    )
                }
        }
    }

    internal object Sessions {
        suspend fun authenticate(sessionDurationMinutes: UInt? = null): StytchResult<IB2BAuthData> =
            safeB2BApiCall {
                apiService.authenticateSessions(
                    CommonRequests.Sessions.AuthenticateRequest(
                        sessionDurationMinutes = sessionDurationMinutes?.toInt(),
                    ),
                )
            }

        suspend fun revoke(): StytchResult<BasicData> =
            safeB2BApiCall {
                apiService.revokeSessions()
            }

        suspend fun exchange(
            organizationId: String,
            sessionDurationMinutes: UInt,
            locale: String? = null,
        ): StytchResult<SessionExchangeResponseData> =
            safeB2BApiCall {
                apiService.exchangeSession(
                    B2BRequests.Session.ExchangeRequest(
                        organizationId = organizationId,
                        locale = locale,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                    ),
                )
            }
    }

    internal object Organization {
        suspend fun getOrganization(): StytchResult<OrganizationResponseData> =
            safeB2BApiCall {
                apiService.getOrganization()
            }

        suspend fun updateOrganization(
            organizationName: String? = null,
            organizationSlug: String? = null,
            organizationLogoUrl: String? = null,
            ssoDefaultConnectionId: String? = null,
            ssoJitProvisioning: SsoJitProvisioning? = null,
            ssoJitProvisioningAllowedConnections: List<String>? = null,
            emailAllowedDomains: List<String>? = null,
            emailJitProvisioning: EmailJitProvisioning? = null,
            emailInvites: EmailInvites? = null,
            authMethods: AuthMethods? = null,
            allowedAuthMethods: List<AllowedAuthMethods>? = null,
            mfaMethods: MfaMethods? = null,
            allowedMfaMethods: List<MfaMethod>? = null,
            mfaPolicy: MfaPolicy? = null,
            rbacEmailImplicitRoleAssignments: List<String>? = null,
        ): StytchResult<OrganizationUpdateResponseData> =
            safeB2BApiCall {
                apiService.updateOrganization(
                    B2BRequests.Organization.UpdateRequest(
                        organizationName = organizationName,
                        organizationSlug = organizationSlug,
                        organizationLogoUrl = organizationLogoUrl,
                        ssoDefaultConnectionId = ssoDefaultConnectionId,
                        ssoJitProvisioning = ssoJitProvisioning,
                        ssoJitProvisioningAllowedConnections = ssoJitProvisioningAllowedConnections,
                        emailAllowedDomains = emailAllowedDomains,
                        emailJitProvisioning = emailJitProvisioning,
                        emailInvites = emailInvites,
                        authMethods = authMethods,
                        allowedAuthMethods = allowedAuthMethods,
                        mfaMethods = mfaMethods,
                        allowedMfaMethods = allowedMfaMethods,
                        mfaPolicy = mfaPolicy,
                        rbacEmailImplicitRoleAssignments = rbacEmailImplicitRoleAssignments,
                    ),
                )
            }

        suspend fun deleteOrganization(): StytchResult<OrganizationDeleteResponseData> =
            safeB2BApiCall {
                apiService.deleteOrganization()
            }

        suspend fun deleteOrganizationMember(memberId: String): StytchResult<OrganizationMemberDeleteResponseData> =
            safeB2BApiCall {
                apiService.deleteOrganizationMember(memberId = memberId)
            }
    }

    internal object Member {
        suspend fun getMember(): StytchResult<MemberResponseData> =
            safeB2BApiCall {
                apiService.getMember()
            }

        suspend fun updateMember(
            name: String?,
            untrustedMetadata: Map<String, Any?>?,
            mfaEnrolled: Boolean?,
            mfaPhoneNumber: String?,
            defaultMfaMethod: MfaMethod?,
        ): StytchResult<UpdateMemberResponseData> =
            safeB2BApiCall {
                apiService.updateMember(
                    B2BRequests.Member.UpdateRequest(
                        name = name,
                        untrustedMetadata = untrustedMetadata,
                        mfaEnrolled = mfaEnrolled,
                        mfaPhoneNumber = mfaPhoneNumber,
                        defaultMfaMethod = defaultMfaMethod,
                    ),
                )
            }

        suspend fun deleteMFAPhoneNumber(): StytchResult<MemberDeleteAuthenticationFactorData> =
            safeB2BApiCall {
                apiService.deleteMFAPhoneNumber()
            }

        suspend fun deleteMFATOTP(): StytchResult<MemberDeleteAuthenticationFactorData> =
            safeB2BApiCall {
                apiService.deleteMFATOTP()
            }

        suspend fun deletePassword(id: String): StytchResult<MemberDeleteAuthenticationFactorData> =
            safeB2BApiCall {
                apiService.deletePassword(id = id)
            }
    }

    internal object Passwords {
        suspend fun authenticate(
            organizationId: String,
            emailAddress: String,
            password: String,
            sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
        ): StytchResult<PasswordsAuthenticateResponseData> =
            safeB2BApiCall {
                apiService.authenticatePassword(
                    B2BRequests.Passwords.AuthenticateRequest(
                        organizationId = organizationId,
                        emailAddress = emailAddress,
                        password = password,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                    ),
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
        ): StytchResult<BasicData> =
            safeB2BApiCall {
                apiService.resetPasswordByEmailStart(
                    B2BRequests.Passwords.ResetByEmailStartRequest(
                        organizationId = organizationId,
                        emailAddress = emailAddress,
                        loginRedirectUrl = loginRedirectUrl,
                        resetPasswordRedirectUrl = resetPasswordRedirectUrl,
                        resetPasswordExpirationMinutes = resetPasswordExpirationMinutes?.toInt(),
                        resetPasswordTemplateId = resetPasswordTemplateId,
                        codeChallenge = codeChallenge,
                    ),
                )
            }

        suspend fun resetByEmail(
            passwordResetToken: String,
            password: String,
            sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
            codeVerifier: String,
        ): StytchResult<EmailResetResponseData> =
            safeB2BApiCall {
                apiService.resetPasswordByEmail(
                    B2BRequests.Passwords.ResetByEmailRequest(
                        passwordResetToken = passwordResetToken,
                        password = password,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                        codeVerifier = codeVerifier,
                    ),
                )
            }

        suspend fun resetByExisting(
            organizationId: String,
            emailAddress: String,
            existingPassword: String,
            newPassword: String,
            sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
        ): StytchResult<B2BAuthData> =
            safeB2BApiCall {
                apiService.resetPasswordByExisting(
                    B2BRequests.Passwords.ResetByExistingPasswordRequest(
                        organizationId = organizationId,
                        emailAddress = emailAddress,
                        existingPassword = existingPassword,
                        newPassword = newPassword,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                    ),
                )
            }

        suspend fun resetBySession(
            organizationId: String,
            password: String,
        ): StytchResult<SessionResetResponseData> =
            safeB2BApiCall {
                apiService.resetPasswordBySession(
                    B2BRequests.Passwords.ResetBySessionRequest(
                        organizationId = organizationId,
                        password = password,
                    ),
                )
            }

        suspend fun strengthCheck(
            email: String?,
            password: String,
        ): StytchResult<StrengthCheckResponseData> =
            safeB2BApiCall {
                apiService.passwordStrengthCheck(
                    B2BRequests.Passwords.StrengthCheckRequest(
                        email = email,
                        password = password,
                    ),
                )
            }
    }

    internal object Discovery {
        suspend fun discoverOrganizations(
            intermediateSessionToken: String?,
        ): StytchResult<DiscoveredOrganizationsResponseData> =
            safeB2BApiCall {
                apiService.discoverOrganizations(
                    B2BRequests.Discovery.MembershipsRequest(
                        intermediateSessionToken = intermediateSessionToken,
                    ),
                )
            }

        suspend fun exchangeSession(
            intermediateSessionToken: String,
            organizationId: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<IntermediateSessionExchangeResponseData> =
            safeB2BApiCall {
                apiService.intermediateSessionExchange(
                    B2BRequests.Discovery.SessionExchangeRequest(
                        intermediateSessionToken = intermediateSessionToken,
                        organizationId = organizationId,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun createOrganization(
            intermediateSessionToken: String,
            sessionDurationMinutes: UInt,
            organizationName: String?,
            organizationSlug: String?,
            organizationLogoUrl: String?,
            ssoJitProvisioning: SsoJitProvisioning?,
            emailAllowedDomains: List<String>?,
            emailJitProvisioning: EmailJitProvisioning?,
            emailInvites: EmailInvites?,
            authMethods: AuthMethods?,
            allowedAuthMethods: List<AllowedAuthMethods>?,
        ): StytchResult<OrganizationCreateResponseData> =
            safeB2BApiCall {
                apiService.createOrganization(
                    B2BRequests.Discovery.CreateRequest(
                        intermediateSessionToken = intermediateSessionToken,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                        organizationName = organizationName,
                        organizationSlug = organizationSlug,
                        organizationLogoUrl = organizationLogoUrl,
                        ssoJitProvisioning = ssoJitProvisioning,
                        emailAllowedDomains = emailAllowedDomains,
                        emailJitProvisioning = emailJitProvisioning,
                        emailInvites = emailInvites,
                        authMethods = authMethods,
                        allowedAuthMethods = allowedAuthMethods,
                    ),
                )
            }
    }

    internal object SSO {
        suspend fun authenticate(
            ssoToken: String,
            sessionDurationMinutes: UInt,
            codeVerifier: String,
        ): StytchResult<SSOAuthenticateResponseData> =
            safeB2BApiCall {
                apiService.ssoAuthenticate(
                    B2BRequests.SSO.AuthenticateRequest(
                        ssoToken = ssoToken,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                        codeVerifier = codeVerifier,
                    ),
                )
            }
    }

    internal object Events {
        suspend fun logEvent(
            eventId: String,
            appSessionId: String,
            persistentId: String,
            clientSentAt: String,
            timezone: String,
            eventName: String,
            infoHeaderModel: InfoHeaderModel,
            details: Map<String, Any>? = null,
            error: Exception? = null,
        ): NoResponseResponse =
            safeB2BApiCall {
                apiService.logEvent(
                    listOf(
                        CommonRequests.Events.Event(
                            telemetry =
                                CommonRequests.Events.EventTelemetry(
                                    eventId = eventId,
                                    appSessionId = appSessionId,
                                    persistentId = persistentId,
                                    clientSentAt = clientSentAt,
                                    timezone = timezone,
                                    app =
                                        CommonRequests.Events.VersionIdentifier(
                                            identifier = infoHeaderModel.app.identifier,
                                            version = infoHeaderModel.app.version,
                                        ),
                                    sdk =
                                        CommonRequests.Events.VersionIdentifier(
                                            identifier = infoHeaderModel.sdk.identifier,
                                            version = infoHeaderModel.sdk.version,
                                        ),
                                    os =
                                        CommonRequests.Events.VersionIdentifier(
                                            identifier = infoHeaderModel.os.identifier,
                                            version = infoHeaderModel.os.version,
                                        ),
                                    device =
                                        CommonRequests.Events.DeviceIdentifier(
                                            model = infoHeaderModel.device.identifier,
                                            screenSize = infoHeaderModel.device.version,
                                        ),
                                ),
                            event =
                                CommonRequests.Events.EventEvent(
                                    publicToken = publicToken,
                                    eventName = eventName,
                                    details = details,
                                    errorDescription = error?.message,
                                ),
                        ),
                    ),
                )
                // Endpoint returns null, but we expect _something_
                StytchDataResponse(NoResponseData())
            }
    }

    suspend fun getBootstrapData(): StytchResult<BootstrapData> =
        safeB2BApiCall {
            apiService.getBootstrapData(publicToken = publicToken)
        }
}
