package com.stytch.sdk.b2b.network.models

import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.OTPSendResponseData

internal object B2BResponses {
    object MagicLinks {
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: B2BEMLAuthenticateData,
        ) : StytchDataResponse<B2BEMLAuthenticateData>(data)

        @JsonClass(generateAdapter = true)
        class DiscoveryAuthenticateResponse(
            data: DiscoveryAuthenticateResponseData,
        ) : StytchDataResponse<DiscoveryAuthenticateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class InviteResponse(
            data: MemberResponseData,
        ) : StytchDataResponse<MemberResponseData>(data)
    }

    object Sessions {
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: SessionsAuthenticateResponseData,
        ) : StytchDataResponse<SessionsAuthenticateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class ExchangeResponse(
            data: SessionExchangeResponseData,
        ) : StytchDataResponse<SessionExchangeResponseData>(data)
    }

    object Organizations {
        @JsonClass(generateAdapter = true)
        class GetOrganizationResponse(
            data: OrganizationResponseData,
        ) : StytchDataResponse<OrganizationResponseData>(data)

        @JsonClass(generateAdapter = true)
        class UpdateOrganizationResponse(
            data: OrganizationUpdateResponseData,
        ) : StytchDataResponse<OrganizationUpdateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class DeleteOrganizationResponse(
            data: OrganizationDeleteResponseData,
        ) : StytchDataResponse<OrganizationDeleteResponseData>(data)

        @JsonClass(generateAdapter = true)
        class DeleteMemberResponse(
            data: OrganizationMemberDeleteResponseData,
        ) : StytchDataResponse<OrganizationMemberDeleteResponseData>(data)

        @JsonClass(generateAdapter = true)
        class ReactivateMemberResponse(
            data: MemberResponseCommonData,
        ) : StytchDataResponse<MemberResponseCommonData>(data)

        @JsonClass(generateAdapter = true)
        class DeleteOrganizationMemberAuthenticationFactorResponse(
            data: MemberResponseCommonData,
        ) : StytchDataResponse<MemberResponseCommonData>(data)

        @JsonClass(generateAdapter = true)
        class GetMemberResponse(
            data: MemberResponseData,
        ) : StytchDataResponse<MemberResponseData>(data)

        @JsonClass(generateAdapter = true)
        class UpdateMemberResponse(
            data: UpdateMemberResponseData,
        ) : StytchDataResponse<UpdateMemberResponseData>(data)

        @JsonClass(generateAdapter = true)
        class DeleteMemberAuthenticationFactorResponse(
            data: MemberDeleteAuthenticationFactorData,
        ) : StytchDataResponse<MemberDeleteAuthenticationFactorData>(data)

        @JsonClass(generateAdapter = true)
        class CreateMemberResponse(
            data: MemberResponseCommonData,
        ) : StytchDataResponse<MemberResponseCommonData>(data)

        @JsonClass(generateAdapter = true)
        class UpdateOrganizationMemberResponse(
            data: MemberResponseCommonData,
        ) : StytchDataResponse<MemberResponseCommonData>(data)

        @JsonClass(generateAdapter = true)
        class MemberSearchResponse(
            data: MemberSearchResponseData,
        ) : StytchDataResponse<MemberSearchResponseData>(data)
    }

    object Passwords {
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: PasswordsAuthenticateResponseData,
        ) : StytchDataResponse<PasswordsAuthenticateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class ResetByEmailStartResponse(
            data: BasicData,
        ) : StytchDataResponse<BasicData>(data)

        @JsonClass(generateAdapter = true)
        class ResetByEmailResponse(
            data: EmailResetResponseData,
        ) : StytchDataResponse<EmailResetResponseData>(data)

        @JsonClass(generateAdapter = true)
        class ResetByExistingPasswordResponse(
            data: PasswordResetByExistingPasswordResponseData,
        ) : StytchDataResponse<PasswordResetByExistingPasswordResponseData>(data)

        @JsonClass(generateAdapter = true)
        class ResetBySessionResponse(
            data: SessionResetResponseData,
        ) : StytchDataResponse<SessionResetResponseData>(data)

        @JsonClass(generateAdapter = true)
        class StrengthCheckResponse(
            data: StrengthCheckResponseData,
        ) : StytchDataResponse<StrengthCheckResponseData>(data)

        object Discovery {
            @JsonClass(generateAdapter = true)
            class ResetByEmailStartResponse(
                data: BasicData,
            ) : StytchDataResponse<BasicData>(data)

            @JsonClass(generateAdapter = true)
            class ResetByEmailResponse(
                data: B2BPasswordDiscoveryResetByEmailResponseData,
            ) : StytchDataResponse<B2BPasswordDiscoveryResetByEmailResponseData>(data)

            @JsonClass(generateAdapter = true)
            class AuthenticateResponse(
                data: B2BPasswordDiscoveryAuthenticateResponseData,
            ) : StytchDataResponse<B2BPasswordDiscoveryAuthenticateResponseData>(data)
        }
    }

    object Discovery {
        @JsonClass(generateAdapter = true)
        class DiscoverOrganizationsResponse(
            data: DiscoveredOrganizationsResponseData,
        ) : StytchDataResponse<DiscoveredOrganizationsResponseData>(data)

        @JsonClass(generateAdapter = true)
        class SessionExchangeResponse(
            data: IntermediateSessionExchangeResponseData,
        ) : StytchDataResponse<IntermediateSessionExchangeResponseData>(data)

        @JsonClass(generateAdapter = true)
        class CreateOrganizationResponse(
            data: OrganizationCreateResponseData,
        ) : StytchDataResponse<OrganizationCreateResponseData>(data)
    }

    object SSO {
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: SSOAuthenticateResponseData,
        ) : StytchDataResponse<SSOAuthenticateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSSOGetConnectionsResponse(
            data: B2BSSOGetConnectionsResponseData,
        ) : StytchDataResponse<B2BSSOGetConnectionsResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSSODeleteConnectionResponse(
            data: B2BSSODeleteConnectionResponseData,
        ) : StytchDataResponse<B2BSSODeleteConnectionResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSSOSAMLCreateConnectionResponse(
            data: B2BSSOSAMLCreateConnectionResponseData,
        ) : StytchDataResponse<B2BSSOSAMLCreateConnectionResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSSOOIDCCreateConnectionResponse(
            data: B2BSSOOIDCCreateConnectionResponseData,
        ) : StytchDataResponse<B2BSSOOIDCCreateConnectionResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSSOSAMLUpdateConnectionResponse(
            data: B2BSSOSAMLUpdateConnectionResponseData,
        ) : StytchDataResponse<B2BSSOSAMLUpdateConnectionResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSSOOIDCUpdateConnectionResponse(
            data: B2BSSOOIDCUpdateConnectionResponseData,
        ) : StytchDataResponse<B2BSSOOIDCUpdateConnectionResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSSOSAMLUpdateConnectionByURLResponse(
            data: B2BSSOSAMLUpdateConnectionByURLResponseData,
        ) : StytchDataResponse<B2BSSOSAMLUpdateConnectionByURLResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSSOSAMLDeleteVerificationCertificateResponse(
            data: B2BSSOSAMLDeleteVerificationCertificateResponseData,
        ) : StytchDataResponse<B2BSSOSAMLDeleteVerificationCertificateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSSODiscoveryConnectionResponse(
            data: B2BSSODiscoveryConnectionResponseData,
        ) : StytchDataResponse<B2BSSODiscoveryConnectionResponseData>(data)
    }

    object OTP {
        object SMS {
            @JsonClass(generateAdapter = true)
            class SendResponse(
                data: OTPSendResponseData,
            ) : StytchDataResponse<OTPSendResponseData>(data)

            @JsonClass(generateAdapter = true)
            class AuthenticateResponse(
                data: SMSAuthenticateResponseData,
            ) : StytchDataResponse<SMSAuthenticateResponseData>(data)
        }

        object Email {
            @JsonClass(generateAdapter = true)
            class LoginOrSignupResponse(
                data: B2BOTPsEmailLoginOrSignupResponseData,
            ) : StytchDataResponse<B2BOTPsEmailLoginOrSignupResponseData>(data)

            @JsonClass(generateAdapter = true)
            class AuthenticateResponse(
                data: B2BOTPsEmailAuthenticateResponseData,
            ) : StytchDataResponse<B2BOTPsEmailAuthenticateResponseData>(data)

            object Discovery {
                @JsonClass(generateAdapter = true)
                class SendResponse(
                    data: B2BDiscoveryOTPEmailSendResponseData,
                ) : StytchDataResponse<B2BDiscoveryOTPEmailSendResponseData>(data)

                @JsonClass(generateAdapter = true)
                class AuthenticateResponse(
                    data: B2BDiscoveryOTPEmailAuthenticateResponseData,
                ) : StytchDataResponse<B2BDiscoveryOTPEmailAuthenticateResponseData>(data)
            }
        }
    }

    object TOTP {
        @JsonClass(generateAdapter = true)
        class CreateResponse(
            data: TOTPCreateResponseData,
        ) : StytchDataResponse<TOTPCreateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: TOTPAuthenticateResponseData,
        ) : StytchDataResponse<TOTPAuthenticateResponseData>(data)
    }

    object RecoveryCodes {
        @JsonClass(generateAdapter = true)
        class GetResponse(
            data: RecoveryCodeGetResponseData,
        ) : StytchDataResponse<RecoveryCodeGetResponseData>(data)

        @JsonClass(generateAdapter = true)
        class RotateResponse(
            data: RecoveryCodeRotateResponseData,
        ) : StytchDataResponse<RecoveryCodeRotateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class RecoverResponse(
            data: RecoveryCodeRecoverResponseData,
        ) : StytchDataResponse<RecoveryCodeRecoverResponseData>(data)
    }

    object OAuth {
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: OAuthAuthenticateResponseData,
        ) : StytchDataResponse<OAuthAuthenticateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class DiscoveryAuthenticateResponse(
            data: DiscoveryAuthenticateResponseData,
        ) : StytchDataResponse<DiscoveryAuthenticateResponseData>(data)
    }

    object SearchManager {
        @JsonClass(generateAdapter = true)
        class SearchOrganizationResponse(
            data: B2BSearchOrganizationResponseData,
        ) : StytchDataResponse<B2BSearchOrganizationResponseData>(data)

        @JsonClass(generateAdapter = true)
        class SearchMemberResponse(
            data: B2BSearchMemberResponseData,
        ) : StytchDataResponse<B2BSearchMemberResponseData>(data)
    }

    object SCIM {
        @JsonClass(generateAdapter = true)
        class B2BSCIMCreateConnectionResponse(
            data: B2BSCIMCreateConnectionResponseData,
        ) : StytchDataResponse<B2BSCIMCreateConnectionResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSCIMUpdateConnectionResponse(
            data: B2BSCIMUpdateConnectionResponseData,
        ) : StytchDataResponse<B2BSCIMUpdateConnectionResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSCIMDeleteConnectionResponse(
            data: B2BSCIMDeleteConnectionResponseData,
        ) : StytchDataResponse<B2BSCIMDeleteConnectionResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSCIMGetConnectionResponse(
            data: B2BSCIMGetConnectionResponseData,
        ) : StytchDataResponse<B2BSCIMGetConnectionResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSCIMGetConnectionGroupsResponse(
            data: B2BSCIMGetConnectionGroupsResponseData,
        ) : StytchDataResponse<B2BSCIMGetConnectionGroupsResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSCIMRotateStartResponse(
            data: B2BSCIMRotateStartResponseData,
        ) : StytchDataResponse<B2BSCIMRotateStartResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSCIMRotateCompleteResponse(
            data: B2BSCIMRotateCompleteResponseData,
        ) : StytchDataResponse<B2BSCIMRotateCompleteResponseData>(data)

        @JsonClass(generateAdapter = true)
        class B2BSCIMRotateCancelResponse(
            data: B2BSCIMRotateCancelResponseData,
        ) : StytchDataResponse<B2BSCIMRotateCancelResponseData>(data)
    }
}
