package com.stytch.sdk.b2b.network.models

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.OTPSendResponseData

internal object B2BResponses {
    object MagicLinks {
        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: B2BEMLAuthenticateData,
        ) : StytchDataResponse<B2BEMLAuthenticateData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class DiscoveryAuthenticateResponse(
            data: DiscoveryAuthenticateResponseData,
        ) : StytchDataResponse<DiscoveryAuthenticateResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class InviteResponse(
            data: MemberResponseData,
        ) : StytchDataResponse<MemberResponseData>(data)
    }

    object Sessions {
        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: SessionsAuthenticateResponseData,
        ) : StytchDataResponse<SessionsAuthenticateResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class ExchangeResponse(
            data: SessionExchangeResponseData,
        ) : StytchDataResponse<SessionExchangeResponseData>(data)
    }

    object Organizations {
        @Keep
        @JsonClass(generateAdapter = true)
        class GetOrganizationResponse(
            data: OrganizationResponseData,
        ) : StytchDataResponse<OrganizationResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class UpdateOrganizationResponse(
            data: OrganizationUpdateResponseData,
        ) : StytchDataResponse<OrganizationUpdateResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class DeleteOrganizationResponse(
            data: OrganizationDeleteResponseData,
        ) : StytchDataResponse<OrganizationDeleteResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class DeleteMemberResponse(
            data: OrganizationMemberDeleteResponseData,
        ) : StytchDataResponse<OrganizationMemberDeleteResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class ReactivateMemberResponse(
            data: MemberResponseCommonData,
        ) : StytchDataResponse<MemberResponseCommonData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class DeleteOrganizationMemberAuthenticationFactorResponse(
            data: MemberResponseCommonData,
        ) : StytchDataResponse<MemberResponseCommonData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class GetMemberResponse(
            data: MemberResponseData,
        ) : StytchDataResponse<MemberResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class UpdateMemberResponse(
            data: UpdateMemberResponseData,
        ) : StytchDataResponse<UpdateMemberResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class DeleteMemberAuthenticationFactorResponse(
            data: MemberDeleteAuthenticationFactorData,
        ) : StytchDataResponse<MemberDeleteAuthenticationFactorData>(data)

        @JsonClass(generateAdapter = true)
        class CreateMemberResponse(
            data: MemberResponseCommonData,
        ) : StytchDataResponse<MemberResponseCommonData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class UpdateOrganizationMemberResponse(
            data: MemberResponseCommonData,
        ) : StytchDataResponse<MemberResponseCommonData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class MemberSearchResponse(
            data: MemberSearchResponseData,
        ) : StytchDataResponse<MemberSearchResponseData>(data)
    }

    object Passwords {
        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: PasswordsAuthenticateResponseData,
        ) : StytchDataResponse<PasswordsAuthenticateResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class ResetByEmailStartResponse(
            data: BasicData,
        ) : StytchDataResponse<BasicData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class ResetByEmailResponse(
            data: EmailResetResponseData,
        ) : StytchDataResponse<EmailResetResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class ResetByExistingPasswordResponse(
            data: PasswordResetByExistingPasswordResponseData,
        ) : StytchDataResponse<PasswordResetByExistingPasswordResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class ResetBySessionResponse(
            data: SessionResetResponseData,
        ) : StytchDataResponse<SessionResetResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class StrengthCheckResponse(
            data: StrengthCheckResponseData,
        ) : StytchDataResponse<StrengthCheckResponseData>(data)

        object Discovery {
            @Keep
            @JsonClass(generateAdapter = true)
            class ResetByEmailStartResponse(
                data: BasicData,
            ) : StytchDataResponse<BasicData>(data)

            @Keep
            @JsonClass(generateAdapter = true)
            class ResetByEmailResponse(
                data: B2BPasswordDiscoveryResetByEmailResponseData,
            ) : StytchDataResponse<B2BPasswordDiscoveryResetByEmailResponseData>(data)

            @Keep
            @JsonClass(generateAdapter = true)
            class AuthenticateResponse(
                data: B2BPasswordDiscoveryAuthenticateResponseData,
            ) : StytchDataResponse<B2BPasswordDiscoveryAuthenticateResponseData>(data)
        }
    }

    object Discovery {
        @Keep
        @JsonClass(generateAdapter = true)
        class DiscoverOrganizationsResponse(
            data: DiscoveredOrganizationsResponseData,
        ) : StytchDataResponse<DiscoveredOrganizationsResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class SessionExchangeResponse(
            data: IntermediateSessionExchangeResponseData,
        ) : StytchDataResponse<IntermediateSessionExchangeResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class CreateOrganizationResponse(
            data: OrganizationCreateResponseData,
        ) : StytchDataResponse<OrganizationCreateResponseData>(data)
    }

    object SSO {
        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: SSOAuthenticateResponseData,
        ) : StytchDataResponse<SSOAuthenticateResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSSOGetConnectionsResponse(
            data: B2BSSOGetConnectionsResponseData,
        ) : StytchDataResponse<B2BSSOGetConnectionsResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSSODeleteConnectionResponse(
            data: B2BSSODeleteConnectionResponseData,
        ) : StytchDataResponse<B2BSSODeleteConnectionResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSSOSAMLCreateConnectionResponse(
            data: B2BSSOSAMLCreateConnectionResponseData,
        ) : StytchDataResponse<B2BSSOSAMLCreateConnectionResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSSOOIDCCreateConnectionResponse(
            data: B2BSSOOIDCCreateConnectionResponseData,
        ) : StytchDataResponse<B2BSSOOIDCCreateConnectionResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSSOSAMLUpdateConnectionResponse(
            data: B2BSSOSAMLUpdateConnectionResponseData,
        ) : StytchDataResponse<B2BSSOSAMLUpdateConnectionResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSSOOIDCUpdateConnectionResponse(
            data: B2BSSOOIDCUpdateConnectionResponseData,
        ) : StytchDataResponse<B2BSSOOIDCUpdateConnectionResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSSOSAMLUpdateConnectionByURLResponse(
            data: B2BSSOSAMLUpdateConnectionByURLResponseData,
        ) : StytchDataResponse<B2BSSOSAMLUpdateConnectionByURLResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSSOSAMLDeleteVerificationCertificateResponse(
            data: B2BSSOSAMLDeleteVerificationCertificateResponseData,
        ) : StytchDataResponse<B2BSSOSAMLDeleteVerificationCertificateResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSSODiscoveryConnectionResponse(
            data: B2BSSODiscoveryConnectionResponseData,
        ) : StytchDataResponse<B2BSSODiscoveryConnectionResponseData>(data)
    }

    object OTP {
        object SMS {
            @Keep
            @JsonClass(generateAdapter = true)
            class SendResponse(
                data: OTPSendResponseData,
            ) : StytchDataResponse<OTPSendResponseData>(data)

            @Keep
            @JsonClass(generateAdapter = true)
            class AuthenticateResponse(
                data: SMSAuthenticateResponseData,
            ) : StytchDataResponse<SMSAuthenticateResponseData>(data)
        }

        object Email {
            @Keep
            @JsonClass(generateAdapter = true)
            class LoginOrSignupResponse(
                data: B2BOTPsEmailLoginOrSignupResponseData,
            ) : StytchDataResponse<B2BOTPsEmailLoginOrSignupResponseData>(data)

            @Keep
            @JsonClass(generateAdapter = true)
            class AuthenticateResponse(
                data: B2BOTPsEmailAuthenticateResponseData,
            ) : StytchDataResponse<B2BOTPsEmailAuthenticateResponseData>(data)

            object Discovery {
                @Keep
                @JsonClass(generateAdapter = true)
                class SendResponse(
                    data: B2BDiscoveryOTPEmailSendResponseData,
                ) : StytchDataResponse<B2BDiscoveryOTPEmailSendResponseData>(data)

                @Keep
                @JsonClass(generateAdapter = true)
                class AuthenticateResponse(
                    data: B2BDiscoveryOTPEmailAuthenticateResponseData,
                ) : StytchDataResponse<B2BDiscoveryOTPEmailAuthenticateResponseData>(data)
            }
        }
    }

    object TOTP {
        @Keep
        @JsonClass(generateAdapter = true)
        class CreateResponse(
            data: TOTPCreateResponseData,
        ) : StytchDataResponse<TOTPCreateResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: TOTPAuthenticateResponseData,
        ) : StytchDataResponse<TOTPAuthenticateResponseData>(data)
    }

    object RecoveryCodes {
        @Keep
        @JsonClass(generateAdapter = true)
        class GetResponse(
            data: RecoveryCodeGetResponseData,
        ) : StytchDataResponse<RecoveryCodeGetResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class RotateResponse(
            data: RecoveryCodeRotateResponseData,
        ) : StytchDataResponse<RecoveryCodeRotateResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class RecoverResponse(
            data: RecoveryCodeRecoverResponseData,
        ) : StytchDataResponse<RecoveryCodeRecoverResponseData>(data)
    }

    object OAuth {
        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: OAuthAuthenticateResponseData,
        ) : StytchDataResponse<OAuthAuthenticateResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class DiscoveryAuthenticateResponse(
            data: DiscoveryAuthenticateResponseData,
        ) : StytchDataResponse<DiscoveryAuthenticateResponseData>(data)
    }

    object SearchManager {
        @Keep
        @JsonClass(generateAdapter = true)
        class SearchOrganizationResponse(
            data: B2BSearchOrganizationResponseData,
        ) : StytchDataResponse<B2BSearchOrganizationResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class SearchMemberResponse(
            data: B2BSearchMemberResponseData,
        ) : StytchDataResponse<B2BSearchMemberResponseData>(data)
    }

    object SCIM {
        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSCIMCreateConnectionResponse(
            data: B2BSCIMCreateConnectionResponseData,
        ) : StytchDataResponse<B2BSCIMCreateConnectionResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSCIMUpdateConnectionResponse(
            data: B2BSCIMUpdateConnectionResponseData,
        ) : StytchDataResponse<B2BSCIMUpdateConnectionResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSCIMDeleteConnectionResponse(
            data: B2BSCIMDeleteConnectionResponseData,
        ) : StytchDataResponse<B2BSCIMDeleteConnectionResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSCIMGetConnectionResponse(
            data: B2BSCIMGetConnectionResponseData,
        ) : StytchDataResponse<B2BSCIMGetConnectionResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSCIMGetConnectionGroupsResponse(
            data: B2BSCIMGetConnectionGroupsResponseData,
        ) : StytchDataResponse<B2BSCIMGetConnectionGroupsResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSCIMRotateStartResponse(
            data: B2BSCIMRotateStartResponseData,
        ) : StytchDataResponse<B2BSCIMRotateStartResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSCIMRotateCompleteResponse(
            data: B2BSCIMRotateCompleteResponseData,
        ) : StytchDataResponse<B2BSCIMRotateCompleteResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class B2BSCIMRotateCancelResponse(
            data: B2BSCIMRotateCancelResponseData,
        ) : StytchDataResponse<B2BSCIMRotateCancelResponseData>(data)
    }
}
