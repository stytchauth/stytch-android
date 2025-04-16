package com.stytch.sdk.common.errors
import java.util.Locale

public enum class StytchAPIErrorType(public val type: String) {
    ACTION_AVAILABLE_ONLY_FOR_ACTIVE_MEMBERS(
        type = "action_available_only_for_active_members",
    ),
    ACTION_AVAILABLE_ONLY_ONCE_PRIMARY_AUTHENTICATION_IS_MET(
        type = "action_available_only_once_primary_authentication_is_met",
    ),
    ACTION_NOT_ALLOWED_EMAIL_DOMAIN_IS_CLAIMED(
        type = "action_not_allowed_email_domain_is_claimed",
    ),
    ACTIVE_SCIM_CONNECTION_EXISTS(
        type = "active_scim_connection_exists",
    ),
    ACTIVE_TOTP_EXISTS(
        type = "active_totp_exists",
    ),
    AD_BLOCKER_DETECTED(
        type = "ad_blocker_detected",
    ),
    ALLOW_LIST_MAX_EXCEEDED(
        type = "allow_list_max_exceeded",
    ),
    ALLOWED_AUTH_METHODS_REQUIRED_FOR_RESTRICTED(
        type = "allowed_auth_methods_required_for_restricted",
    ),
    ALLOWED_MFA_METHODS_REQUIRED_FOR_RESTRICTED(
        type = "allowed_mfa_methods_required_for_restricted",
    ),
    APPLE_OAUTH_CONFIG_NOT_FOUND(
        type = "apple_oauth_config_not_found",
    ),
    ARGON_2_KEY_LENGTH_MISMATCH(
        type = "argon_2_key_length_mismatch",
    ),
    ATTRIBUTE_MISMATCH(
        type = "attribute_mismatch",
    ),
    AUTHORIZATION_ENDPOINT_NOT_CONFIGURED_FOR_PROJECT(
        type = "authorization_endpoint_not_configured_for_project",
    ),
    BAD_APP_BUNDLE_FOR_STYTCH_SDK(
        type = "bad_app_bundle_for_stytch_sdk",
    ),
    BAD_DOMAIN_FOR_STYTCH_SDK(
        type = "bad_domain_for_stytch_sdk",
    ),
    BAD_REQUEST(
        type = "bad_request",
    ),
    BAD_VERTICAL_FOR_STYTCH_SDK(
        type = "bad_vertical_for_stytch_sdk",
    ),
    BILLING_INFO_NOT_FOUND(
        type = "billing_info_not_found",
    ),
    BILLING_NOT_VERIFIED(
        type = "billing_not_verified",
    ),
    BILLING_NOT_VERIFIED_FOR_EMAIL(
        type = "billing_not_verified_for_email",
    ),
    BIOMETRIC_REGISTRATION_NOT_FOUND(
        type = "biometric_registration_not_found",
    ),
    BREACHED_PASSWORD(
        type = "breached_password",
    ),
    BULK_MIGRATE_INVALID_USER_COUNT(
        type = "bulk_migrate_invalid_user_count",
    ),
    CANNOT_ASSIGN_DEFAULT_RBAC_ROLE_ID(
        type = "cannot_assign_default_rbac_role_id",
    ),
    CANNOT_DELETE_DEFAULT_SSO_CONNECTION(
        type = "cannot_delete_default_sso_connection",
    ),
    CANNOT_DELETE_EMAIL(
        type = "cannot_delete_email",
    ),
    CANNOT_DELETE_LAST_PRIMARY_FACTOR(
        type = "cannot_delete_last_primary_factor",
    ),
    CANNOT_DELETE_LAST_SSO_VERIFICATION_KEY(
        type = "cannot_delete_last_sso_verification_key",
    ),
    CANNOT_PRESERVE_EXISTING_SESSIONS(
        type = "cannot_preserve_existing_sessions",
    ),
    CANNOT_REMOVE_PRIMARY_AUTH_MECHANISM(
        type = "cannot_remove_primary_auth_mechanism",
    ),
    CANNOT_RESET_PASSWORD_WITH_EXISTING_PASSWORD(
        type = "cannot_reset_password_with_existing_password",
    ),
    CANNOT_UPDATE_TRUSTED_METADATA(
        type = "cannot_update_trusted_metadata",
    ),
    CANNOT_USE_BIOMETRICS_WITH_PENDING_USER(
        type = "cannot_use_biometrics_with_pending_user",
    ),
    CANNOT_USE_WEBAUTHN_WITH_PENDING_USER(
        type = "cannot_use_webauthn_with_pending_user",
    ),
    CAPTCHA_FAILED(
        type = "captcha_failed",
    ),
    CAPTCHA_PROVIDER_NOT_FOUND(
        type = "captcha_provider_not_found",
    ),
    CAPTCHA_REQUIRED(
        type = "captcha_required",
    ),
    CLAIMED_EMAIL_DOMAINS_NOT_SUPPORTED(
        type = "claimed_email_domains_not_supported",
    ),
    CLIENT_CLOSED_REQUEST(
        type = "client_closed_request",
    ),
    CLIENT_SECRET_TOO_LONG(
        type = "client_secret_too_long",
    ),
    CNAME_INVALID_FOR_HTTP_ONLY_COOKIES(
        type = "cname_invalid_for_http_only_cookies",
    ),
    CNAME_RECORD_NOT_FOUND(
        type = "cname_record_not_found",
    ),
    CNAME_REQUIRED_FOR_HTTP_ONLY_COOKIES(
        type = "cname_required_for_http_only_cookies",
    ),
    COULD_NOT_PARSE_BIOMETRIC_SIGNATURE(
        type = "could_not_parse_biometric_signature",
    ),
    CROSS_ORG_PASSWORDS_ENABLED(
        type = "cross_org_passwords_enabled",
    ),
    CROSS_ORG_PASSWORDS_NOT_ENABLED(
        type = "cross_org_passwords_not_enabled",
    ),
    CRYPTO_WALLET_NOT_FOUND(
        type = "crypto_wallet_not_found",
    ),
    CRYPTO_WALLET_TYPE_INCOMPATIBLE_WITH_SIWE(
        type = "crypto_wallet_type_incompatible_with_siwe",
    ),
    CUSTOM_CLAIMS_TOO_LARGE(
        type = "custom_claims_too_large",
    ),
    DEFAULT_MFA_MEMBER_MISSING_FACTOR(
        type = "default_mfa_member_missing_factor",
    ),
    DEFAULT_PROVIDER_NOT_ALLOWED(
        type = "default_provider_not_allowed",
    ),
    DENY_LIST_MAX_EXCEEDED(
        type = "deny_list_max_exceeded",
    ),
    DEPRECATED_ENDPOINT(
        type = "deprecated_endpoint",
    ),
    DESIRED_EMAIL_DEACTIVATED_BY_DIFFERENT_MEMBER(
        type = "desired_email_deactivated_by_different_member",
    ),
    DUPLICATE_BIOMETRIC_REGISTRATION(
        type = "duplicate_biometric_registration",
    ),
    DUPLICATE_CUSTOM_HOSTNAME_FOUND(
        type = "duplicate_custom_hostname_found",
    ),
    DUPLICATE_EMAIL(
        type = "duplicate_email",
    ),
    DUPLICATE_EMAIL_FOR_USER(
        type = "duplicate_email_for_user",
    ),
    DUPLICATE_EMAIL_TEMPLATE_VANITY_ID(
        type = "duplicate_email_template_vanity_id",
    ),
    DUPLICATE_EXTERNAL_SSO_CONNECTION(
        type = "duplicate_external_sso_connection",
    ),
    DUPLICATE_M2M_CLIENT_ID(
        type = "duplicate_m2m_client_id",
    ),
    DUPLICATE_MEMBER_EMAIL(
        type = "duplicate_member_email",
    ),
    DUPLICATE_MEMBER_EXTERNAL_ID(
        type = "duplicate_member_external_id",
    ),
    DUPLICATE_MEMBER_PHONE_NUMBER(
        type = "duplicate_member_phone_number",
    ),
    DUPLICATE_MEMBER_PHONE_NUMBER_FOR_MEMBER(
        type = "duplicate_member_phone_number_for_member",
    ),
    DUPLICATE_ORGANIZATION(
        type = "duplicate_organization",
    ),
    DUPLICATE_ORGANIZATION_USER(
        type = "duplicate_organization_user",
    ),
    DUPLICATE_PHONE_NUMBER(
        type = "duplicate_phone_number",
    ),
    DUPLICATE_PHONE_NUMBER_FOR_USER(
        type = "duplicate_phone_number_for_user",
    ),
    DUPLICATE_PROJECT_USER(
        type = "duplicate_project_user",
    ),
    DUPLICATE_REDIRECT_URL(
        type = "duplicate_redirect_url",
    ),
    DUPLICATE_SAML_CONNECTION(
        type = "duplicate_saml_connection",
    ),
    DUPLICATE_SAML_RESPONSE(
        type = "duplicate_saml_response",
    ),
    DUPLICATE_USER_EXTERNAL_ID(
        type = "duplicate_user_external_id",
    ),
    DUPLICATE_WALLET_ADDRESS(
        type = "duplicate_wallet_address",
    ),
    DUPLICATE_WALLET_ADDRESS_FOR_USER(
        type = "duplicate_wallet_address_for_user",
    ),
    DUPLICATE_WEBAUTHN_REGISTRATION(
        type = "duplicate_webauthn_registration",
    ),
    DYNAMIC_CLIENT_REGISTRATION_NOT_ENABLED(
        type = "dynamic_client_registration_not_enabled",
    ),
    EMAIL_IN_DENYLIST(
        type = "email_in_denylist",
    ),
    EMAIL_JIT_PROVISIONING_NOT_ALLOWED(
        type = "email_jit_provisioning_not_allowed",
    ),
    EMAIL_NOT_FOUND(
        type = "email_not_found",
    ),
    EMAIL_NOT_IN_ALLOWLIST(
        type = "email_not_in_allowlist",
    ),
    EMAIL_SETTINGS_NOT_FOUND(
        type = "email_settings_not_found",
    ),
    EMAIL_TEMPLATE_NOT_FOUND(
        type = "email_template_not_found",
    ),
    EMAIL_UNVERIFIED_FOR_REACTIVATION(
        type = "email_unverified_for_reactivation",
    ),
    EMAIL_VERIFICATION_REQUIRED(
        type = "email_verification_required",
    ),
    EMPTY_RBAC_RESOURCE_ID(
        type = "empty_rbac_resource_id",
    ),
    EMPTY_RBAC_ROLE_ID(
        type = "empty_rbac_role_id",
    ),
    EMPTY_RBAC_SCOPE(
        type = "empty_rbac_scope",
    ),
    EMPTY_WEBAUTHN_REGISTRATION_NAME(
        type = "empty_webauthn_registration_name",
    ),
    ENDPOINT_NOT_AUTHORIZED_FOR_SDK(
        type = "endpoint_not_authorized_for_sdk",
    ),
    ENTERPRISE_ENDPOINT(
        type = "enterprise_endpoint",
    ),
    EXPIRED_OAUTH_RESPONSE(
        type = "expired_oauth_response",
    ),
    EXPIRED_OIDC_RESPONSE(
        type = "expired_oidc_response",
    ),
    EXPIRED_SAML_RESPONSE(
        type = "expired_saml_response",
    ),
    EXPIRED_TOTP(
        type = "expired_totp",
    ),
    EXTERNAL_CONNECTION_ID_NOT_FOUND(
        type = "external_connection_id_not_found",
    ),
    EXTERNAL_CONNECTION_NOT_ACTIVE(
        type = "external_connection_not_active",
    ),
    EXTERNAL_ORGANIZATION_ID_SAME_AS_ORGANIZATION_ID(
        type = "external_organization_id_same_as_organization_id",
    ),
    FAILED_SAML_RESPONSE(
        type = "failed_saml_response",
    ),
    FORBIDDEN_CHARACTER_ZERO_WIDTH_SPACE(
        type = "forbidden_character_zero_width_space",
    ),
    ID_TOKEN_EXPIRED(
        type = "id_token_expired",
    ),
    ID_TOKEN_INCORRECT_AUDIENCE(
        type = "id_token_incorrect_audience",
    ),
    ID_TOKEN_INVALID(
        type = "id_token_invalid",
    ),
    ID_TOKEN_NONCE_INVALID(
        type = "id_token_nonce_invalid",
    ),
    IDP_ACCESS_TOKEN_COULD_NOT_BE_EXCHANGED(
        type = "idp_access_token_could_not_be_exchanged",
    ),
    IDP_ACCESS_TOKEN_EXPIRED(
        type = "idp_access_token_expired",
    ),
    IDP_ACCESS_TOKEN_NOT_FOUND(
        type = "idp_access_token_not_found",
    ),
    IDP_AUTH_CODE_EXPIRED(
        type = "idp_auth_code_expired",
    ),
    IDP_AUTH_CODE_NOT_FOUND(
        type = "idp_auth_code_not_found",
    ),
    IDP_CLIENT_ALREADY_ROTATING_SECRET(
        type = "idp_client_already_rotating_secret",
    ),
    IDP_CLIENT_INVALID_REDIRECT_URL(
        type = "idp_client_invalid_redirect_url",
    ),
    IDP_CLIENT_MISCONFIGURED_CLIENT(
        type = "idp_client_misconfigured_client",
    ),
    IDP_CLIENT_NOT_FOUND(
        type = "idp_client_not_found",
    ),
    IDP_CLIENT_NOT_ROTATING_SECRET(
        type = "idp_client_not_rotating_secret",
    ),
    IDP_CLIENT_REDIRECT_URL_CANNOT_USE_LOCALHOST(
        type = "idp_client_redirect_url_cannot_use_localhost",
    ),
    IDP_CLIENT_REDIRECT_URL_HTTP_SCHEME_MUST_USE_LOOPBACK(
        type = "idp_client_redirect_url_http_scheme_must_use_loopback",
    ),
    IDP_CLIENT_REDIRECT_URL_MUST_INCLUDE_SCHEME(
        type = "idp_client_redirect_url_must_include_scheme",
    ),
    IDP_CLIENT_REDIRECT_URL_MUST_USE_HTTP_OR_HTTPS_SCHEME(
        type = "idp_client_redirect_url_must_use_http_or_https_scheme",
    ),
    IDP_CLIENT_REDIRECT_URL_MUST_USE_HTTPS_SCHEME(
        type = "idp_client_redirect_url_must_use_https_scheme",
    ),
    IDP_CLIENT_SUPPLIED_REDIRECT_URL_NOT_FOUND_IN_CLIENT(
        type = "idp_client_supplied_redirect_url_not_found_in_client",
    ),
    IDP_INVALID_ACCESS_TOKEN_CUSTOM_AUDIENCE(
        type = "idp_invalid_access_token_custom_audience",
    ),
    IDP_INVALID_ACCESS_TOKEN_EXPIRY_MINUTES(
        type = "idp_invalid_access_token_expiry_minutes",
    ),
    IDP_INVALID_ACCESS_TOKEN_JWT_TEMPLATE(
        type = "idp_invalid_access_token_jwt_template",
    ),
    IDP_REFRESH_TOKEN_ALREADY_USED(
        type = "idp_refresh_token_already_used",
    ),
    IDP_REFRESH_TOKEN_EXPIRED(
        type = "idp_refresh_token_expired",
    ),
    IDP_REFRESH_TOKEN_NOT_FOUND(
        type = "idp_refresh_token_not_found",
    ),
    INACTIVE_EMAIL(
        type = "inactive_email",
    ),
    INCOMPATIBLE_SESSION_TYPE(
        type = "incompatible_session_type",
    ),
    INDETERMINATE_SSO_CONNECTION_FOR_ORGANIZATION(
        type = "indeterminate_sso_connection_for_organization",
    ),
    INSECURE_M2M_CLIENT_SECRET(
        type = "insecure_m2m_client_secret",
    ),
    INSUFFICIENT_FACTORS(
        type = "insufficient_factors",
    ),
    INSUFFICIENT_FACTORS_FOR_SESSION_EXCHANGE(
        type = "insufficient_factors_for_session_exchange",
    ),
    INTERMEDIATE_SESSION_NOT_FOUND(
        type = "intermediate_session_not_found",
    ),
    INTERNAL_SERVER_ERROR(
        type = "internal_server_error",
    ),
    INVALID_APPEND_SALT(
        type = "invalid_append_salt",
    ),
    INVALID_ARGON_2_ITERATION_AMOUNT(
        type = "invalid_argon_2_iteration_amount",
    ),
    INVALID_ARGON_2_KEY_LENGTH(
        type = "invalid_argon_2_key_length",
    ),
    INVALID_ARGON_2_MEMORY(
        type = "invalid_argon_2_memory",
    ),
    INVALID_ARGON_2_SALT(
        type = "invalid_argon_2_salt",
    ),
    INVALID_ARGON_2_THREADS(
        type = "invalid_argon_2_threads",
    ),
    INVALID_ARGUMENT(
        type = "invalid_argument",
    ),
    INVALID_AUDIENCE_SAML_RESPONSE(
        type = "invalid_audience_saml_response",
    ),
    INVALID_AUTHENTICATION_TYPE(
        type = "invalid_authentication_type",
    ),
    INVALID_AUTHENTICATOR_TYPE(
        type = "invalid_authenticator_type",
    ),
    INVALID_AUTHORIZATION_HEADER(
        type = "invalid_authorization_header",
    ),
    INVALID_AUTHORIZATION_URL(
        type = "invalid_authorization_url",
    ),
    INVALID_B2B_ENDPOINT(
        type = "invalid_b2b_endpoint",
    ),
    INVALID_BASE64_SCRYPT_HASH(
        type = "invalid_base64_scrypt_hash",
    ),
    INVALID_BASE64_SCRYPT_SALT(
        type = "invalid_base64_scrypt_salt",
    ),
    INVALID_BCRYPT_COST(
        type = "invalid_bcrypt_cost",
    ),
    INVALID_BCRYPT_HASH(
        type = "invalid_bcrypt_hash",
    ),
    INVALID_BIOMETRIC_REGISTRATION_ID(
        type = "invalid_biometric_registration_id",
    ),
    INVALID_CALLBACK_ID(
        type = "invalid_callback_id",
    ),
    INVALID_CAPTCHA_PROVIDER_ID(
        type = "invalid_captcha_provider_id",
    ),
    INVALID_CAPTCHA_PROVIDER_TYPE(
        type = "invalid_captcha_provider_type",
    ),
    INVALID_CAPTCHA_THRESHOLD(
        type = "invalid_captcha_threshold",
    ),
    INVALID_CLIENT_ID(
        type = "invalid_client_id",
    ),
    INVALID_CLIENT_SECRET(
        type = "invalid_client_secret",
    ),
    INVALID_CODE(
        type = "invalid_code",
    ),
    INVALID_CONNECTION_FOR_JIT_PROVISIONING(
        type = "invalid_connection_for_jit_provisioning",
    ),
    INVALID_CONSUMER_ENDPOINT(
        type = "invalid_consumer_endpoint",
    ),
    INVALID_CREATE_USER_REQUEST(
        type = "invalid_create_user_request",
    ),
    INVALID_CRYPTO_WALLET_ADDRESS(
        type = "invalid_crypto_wallet_address",
    ),
    INVALID_CRYPTO_WALLET_ID(
        type = "invalid_crypto_wallet_id",
    ),
    INVALID_DEFAULT_URL(
        type = "invalid_default_url",
    ),
    INVALID_DISCOVERY_REDIRECT_URL(
        type = "invalid_discovery_redirect_url",
    ),
    INVALID_DISPLAY_NAME(
        type = "invalid_display_name",
    ),
    INVALID_DOMAIN(
        type = "invalid_domain",
    ),
    INVALID_EMAIL(
        type = "invalid_email",
    ),
    INVALID_EMAIL_DOMAIN(
        type = "invalid_email_domain",
    ),
    INVALID_EMAIL_FOR_INVITES(
        type = "invalid_email_for_invites",
    ),
    INVALID_EMAIL_FOR_JIT_PROVISIONING(
        type = "invalid_email_for_jit_provisioning",
    ),
    INVALID_EMAIL_HTML(
        type = "invalid_email_html",
    ),
    INVALID_EMAIL_ID(
        type = "invalid_email_id",
    ),
    INVALID_EMAIL_PLAINTEXT(
        type = "invalid_email_plaintext",
    ),
    INVALID_EMAIL_SANDBOX(
        type = "invalid_email_sandbox",
    ),
    INVALID_EMAIL_SUBJECT(
        type = "invalid_email_subject",
    ),
    INVALID_EMAIL_TEMPLATE_PARAMETERS(
        type = "invalid_email_template_parameters",
    ),
    INVALID_ETHEREUM_ADDRESS(
        type = "invalid_ethereum_address",
    ),
    INVALID_EXCHANGE_PRIMARY_FACTOR_FIELDS(
        type = "invalid_exchange_primary_factor_fields",
    ),
    INVALID_EXCHANGE_PRIMARY_FACTOR_USER(
        type = "invalid_exchange_primary_factor_user",
    ),
    INVALID_EXPIRATION(
        type = "invalid_expiration",
    ),
    INVALID_EXPIRATION_OTP(
        type = "invalid_expiration_otp",
    ),
    INVALID_GOOGLE_HOSTED_DOMAIN_ERROR(
        type = "invalid_google_hosted_domain_error",
    ),
    INVALID_HASH(
        type = "invalid_hash",
    ),
    INVALID_HASH_TYPE(
        type = "invalid_hash_type",
    ),
    INVALID_ID(
        type = "invalid_id",
    ),
    INVALID_IDP_CLIENT_TYPE(
        type = "invalid_idp_client_type",
    ),
    INVALID_IDP_ENTITY_ID(
        type = "invalid_idp_entity_id",
    ),
    INVALID_IDP_SSO_URL(
        type = "invalid_idp_sso_url",
    ),
    INVALID_IMPERSONATION_REASON(
        type = "invalid_impersonation_reason",
    ),
    INVALID_INTERMEDIATE_SESSION_TOKEN_FOR_ORGANIZATION(
        type = "invalid_intermediate_session_token_for_organization",
    ),
    INVALID_INVITE_MAGIC_LINK_URL(
        type = "invalid_invite_magic_link_url",
    ),
    INVALID_INVITE_REDIRECT_URL(
        type = "invalid_invite_redirect_url",
    ),
    INVALID_IP_ADDRESS(
        type = "invalid_ip_address",
    ),
    INVALID_ISSUER(
        type = "invalid_issuer",
    ),
    INVALID_JWKS_URL(
        type = "invalid_jwks_url",
    ),
    INVALID_LOCALE(
        type = "invalid_locale",
    ),
    INVALID_LOGIN_MAGIC_LINK_URL(
        type = "invalid_login_magic_link_url",
    ),
    INVALID_LOGIN_OAUTH_URL(
        type = "invalid_login_oauth_url",
    ),
    INVALID_LOGIN_REDIRECT_URL(
        type = "invalid_login_redirect_url",
    ),
    INVALID_LOGIN_SSO_URL(
        type = "invalid_login_sso_url",
    ),
    INVALID_M2M_CLIENT_SCOPE(
        type = "invalid_m2m_client_scope",
    ),
    INVALID_M2M_CLIENT_STATUS(
        type = "invalid_m2m_client_status",
    ),
    INVALID_MAGIC_LINK_URL(
        type = "invalid_magic_link_url",
    ),
    INVALID_MD_5_HASH(
        type = "invalid_md_5_hash",
    ),
    INVALID_MEMBER_GET_FIELDS(
        type = "invalid_member_get_fields",
    ),
    INVALID_METHOD_ID(
        type = "invalid_method_id",
    ),
    INVALID_MFA_DEFAULT_METHOD(
        type = "invalid_mfa_default_method",
    ),
    INVALID_MICROSOFT_TENANT_TYPE(
        type = "invalid_microsoft_tenant_type",
    ),
    INVALID_MICROSOFT_USER_PRINCIPAL_NAME(
        type = "invalid_microsoft_user_principal_name",
    ),
    INVALID_MOBILE_IDENTIFIER(
        type = "invalid_mobile_identifier",
    ),
    INVALID_OAUTH_ALLOWED_TENANTS_FORMAT(
        type = "invalid_oauth_allowed_tenants_format",
    ),
    INVALID_OAUTH_ATTACH_TOKEN(
        type = "invalid_oauth_attach_token",
    ),
    INVALID_OAUTH_PROVIDER(
        type = "invalid_oauth_provider",
    ),
    INVALID_OAUTH_TENANT_FOR_JIT_PROVISIONING(
        type = "invalid_oauth_tenant_for_jit_provisioning",
    ),
    INVALID_OAUTH_USER_REGISTRATION_ID(
        type = "invalid_oauth_user_registration_id",
    ),
    INVALID_ORGANIZATION_ALLOWED_AUTH_METHODS(
        type = "invalid_organization_allowed_auth_methods",
    ),
    INVALID_ORGANIZATION_ALLOWED_MFA_METHODS(
        type = "invalid_organization_allowed_mfa_methods",
    ),
    INVALID_ORGANIZATION_AUTH_FACTOR_SETTING(
        type = "invalid_organization_auth_factor_setting",
    ),
    INVALID_ORGANIZATION_AUTH_FACTOR_SETTING_FOR_EMAIL_JIT_PROVISIONING(
        type = "invalid_organization_auth_factor_setting_for_email_jit_provisioning",
    ),
    INVALID_ORGANIZATION_AUTH_FACTOR_SETTING_FOR_OAUTH_TENANT_JIT_PROVISIONING(
        type = "invalid_organization_auth_factor_setting_for_oauth_tenant_jit_provisioning",
    ),
    INVALID_ORGANIZATION_AUTH_METHOD_SETTINGS(
        type = "invalid_organization_auth_method_settings",
    ),
    INVALID_ORGANIZATION_ID(
        type = "invalid_organization_id",
    ),
    INVALID_ORGANIZATION_MFA_FACTOR_SETTING(
        type = "invalid_organization_mfa_factor_setting",
    ),
    INVALID_ORGANIZATION_MFA_POLICY(
        type = "invalid_organization_mfa_policy",
    ),
    INVALID_ORGANIZATION_NAME(
        type = "invalid_organization_name",
    ),
    INVALID_ORGANIZATION_SLUG(
        type = "invalid_organization_slug",
    ),
    INVALID_PASSWORD_ID(
        type = "invalid_password_id",
    ),
    INVALID_PASSWORD_RESET_REDIRECT_URL(
        type = "invalid_password_reset_redirect_url",
    ),
    INVALID_PASSWORD_STRENGTH_LUDS_COMPLEXITY(
        type = "invalid_password_strength_luds_complexity",
    ),
    INVALID_PASSWORD_STRENGTH_LUDS_LENGTH(
        type = "invalid_password_strength_luds_length",
    ),
    INVALID_PBKDF_2_ALGORITHM(
        type = "invalid_pbkdf_2_algorithm",
    ),
    INVALID_PBKDF_2_HASH(
        type = "invalid_pbkdf_2_hash",
    ),
    INVALID_PBKDF_2_ITERATION_AMOUNT(
        type = "invalid_pbkdf_2_iteration_amount",
    ),
    INVALID_PBKDF_2_SALT(
        type = "invalid_pbkdf_2_salt",
    ),
    INVALID_PERMISSION_ACTION(
        type = "invalid_permission_action",
    ),
    INVALID_PERMISSION_RESOURCE(
        type = "invalid_permission_resource",
    ),
    INVALID_PHONE_NUMBER(
        type = "invalid_phone_number",
    ),
    INVALID_PHONE_NUMBER_COUNTRY_CODE(
        type = "invalid_phone_number_country_code",
    ),
    INVALID_PHONE_NUMBER_DOCS(
        type = "invalid_phone_number_docs",
    ),
    INVALID_PHONE_NUMBER_SANDBOX(
        type = "invalid_phone_number_sandbox",
    ),
    INVALID_PHPASS_HASH_FORMAT(
        type = "invalid_phpass_hash_format",
    ),
    INVALID_PHPASS_HASH_PREFIX(
        type = "invalid_phpass_hash_prefix",
    ),
    INVALID_PKCE_CODE_CHALLENGE(
        type = "invalid_pkce_code_challenge",
    ),
    INVALID_PKCE_CODE_VERIFIER(
        type = "invalid_pkce_code_verifier",
    ),
    INVALID_PREPEND_SALT(
        type = "invalid_prepend_salt",
    ),
    INVALID_PROJECT_ID(
        type = "invalid_project_id",
    ),
    INVALID_PROJECT_ID_AUTHENTICATION(
        type = "invalid_project_id_authentication",
    ),
    INVALID_PROJECT_NAME(
        type = "invalid_project_name",
    ),
    INVALID_PUBLIC_KEY(
        type = "invalid_public_key",
    ),
    INVALID_PUBLIC_KEY_CREDENTIAL(
        type = "invalid_public_key_credential",
    ),
    INVALID_PUBLIC_KEY_CREDENTIAL_INVALID_AUTHENTICATOR_DATA(
        type = "invalid_public_key_credential_invalid_authenticator_data",
    ),
    INVALID_PUBLIC_KEY_CREDENTIAL_INVALID_ID(
        type = "invalid_public_key_credential_invalid_id",
    ),
    INVALID_PUBLIC_KEY_CREDENTIAL_INVALID_SIGNATURE(
        type = "invalid_public_key_credential_invalid_signature",
    ),
    INVALID_PUBLIC_KEY_CREDENTIAL_MALFORMED_ATTESTATION_OBJECT(
        type = "invalid_public_key_credential_malformed_attestation_object",
    ),
    INVALID_PUBLIC_KEY_CREDENTIAL_MALFORMED_CLIENT_DATA_JSON(
        type = "invalid_public_key_credential_malformed_client_data_json",
    ),
    INVALID_PUBLIC_KEY_CREDENTIAL_MISSING_ATTESTATION_OBJECT_FIELD(
        type = "invalid_public_key_credential_missing_attestation_object_field",
    ),
    INVALID_PUBLIC_KEY_CREDENTIAL_MISSING_AUTHENTICATOR_DATA_FIELD(
        type = "invalid_public_key_credential_missing_authenticator_data_field",
    ),
    INVALID_PUBLIC_KEY_CREDENTIAL_MISSING_CLIENT_DATA_JSON_FIELD(
        type = "invalid_public_key_credential_missing_client_data_json_field",
    ),
    INVALID_PUBLIC_KEY_CREDENTIAL_MISSING_ID_FIELD(
        type = "invalid_public_key_credential_missing_id_field",
    ),
    INVALID_PUBLIC_KEY_CREDENTIAL_MISSING_RESPONSE_FIELD(
        type = "invalid_public_key_credential_missing_response_field",
    ),
    INVALID_PUBLIC_KEY_CREDENTIAL_MISSING_SIGNATURE_FIELD(
        type = "invalid_public_key_credential_missing_signature_field",
    ),
    INVALID_PUBLIC_TOKEN_ID(
        type = "invalid_public_token_id",
    ),
    INVALID_RBAC_CUSTOM_ROLE(
        type = "invalid_rbac_custom_role",
    ),
    INVALID_RBAC_ROLE_ASSIGNMENT(
        type = "invalid_rbac_role_assignment",
    ),
    INVALID_RBAC_ROLE_ID(
        type = "invalid_rbac_role_id",
    ),
    INVALID_RBAC_SCOPE(
        type = "invalid_rbac_scope",
    ),
    INVALID_RBAC_STYTCH_ROLE_EDIT(
        type = "invalid_rbac_stytch_role_edit",
    ),
    INVALID_RECOVERY_CODE(
        type = "invalid_recovery_code",
    ),
    INVALID_REDIRECT_URL_TYPE(
        type = "invalid_redirect_url_type",
    ),
    INVALID_REQUEST_ID(
        type = "invalid_request_id",
    ),
    INVALID_REQUEST_VALUE(
        type = "invalid_request_value",
    ),
    INVALID_RESTRICTED_EMAIL_SETTING(
        type = "invalid_restricted_email_setting",
    ),
    INVALID_RESTRICTED_OAUTH_TENANTS_SETTING(
        type = "invalid_restricted_oauth_tenants_setting",
    ),
    INVALID_RESTRICTED_SSO_SETTING(
        type = "invalid_restricted_sso_setting",
    ),
    INVALID_ROLE(
        type = "invalid_role",
    ),
    INVALID_SAML_METADATA_URL(
        type = "invalid_saml_metadata_url",
    ),
    INVALID_SAML_RESPONSE(
        type = "invalid_saml_response",
    ),
    INVALID_SAML_RESPONSE_EMAIL(
        type = "invalid_saml_response_email",
    ),
    INVALID_SAML_RESPONSE_GROUPS(
        type = "invalid_saml_response_groups",
    ),
    INVALID_SCIM_IDP(
        type = "invalid_scim_idp",
    ),
    INVALID_SCRYPT_N_PARAMETER(
        type = "invalid_scrypt_n_parameter",
    ),
    INVALID_SCRYPT_PARAMETERS(
        type = "invalid_scrypt_parameters",
    ),
    INVALID_SCRYPT_SALT_LENGTH(
        type = "invalid_scrypt_salt_length",
    ),
    INVALID_SECRET_AUTHENTICATION(
        type = "invalid_secret_authentication",
    ),
    INVALID_SECRET_ID(
        type = "invalid_secret_id",
    ),
    INVALID_SESSION_DURATION(
        type = "invalid_session_duration",
    ),
    INVALID_SESSION_DURATION_MINUTES(
        type = "invalid_session_duration_minutes",
    ),
    INVALID_SESSION_FIELDS(
        type = "invalid_session_fields",
    ),
    INVALID_SESSION_ID(
        type = "invalid_session_id",
    ),
    INVALID_SESSION_MANAGEMENT(
        type = "invalid_session_management",
    ),
    INVALID_SESSION_MISSING_PRIMARY_FACTOR(
        type = "invalid_session_missing_primary_factor",
    ),
    INVALID_SESSION_TOKEN(
        type = "invalid_session_token",
    ),
    INVALID_SESSION_TOKEN_DOCS(
        type = "invalid_session_token_docs",
    ),
    INVALID_SHA_1_HASH(
        type = "invalid_sha_1_hash",
    ),
    INVALID_SIGNATURE(
        type = "invalid_signature",
    ),
    INVALID_SIGNATURE_SAML_RESPONSE(
        type = "invalid_signature_saml_response",
    ),
    INVALID_SIGNUP_MAGIC_LINK_URL(
        type = "invalid_signup_magic_link_url",
    ),
    INVALID_SIGNUP_OAUTH_URL(
        type = "invalid_signup_oauth_url",
    ),
    INVALID_SIGNUP_REDIRECT_URL(
        type = "invalid_signup_redirect_url",
    ),
    INVALID_SIGNUP_SSO_URL(
        type = "invalid_signup_sso_url",
    ),
    INVALID_SIWE_CHAIN_ID(
        type = "invalid_siwe_chain_id",
    ),
    INVALID_SIWE_DOMAIN(
        type = "invalid_siwe_domain",
    ),
    INVALID_SIWE_MESSAGE_REQUEST_ID(
        type = "invalid_siwe_message_request_id",
    ),
    INVALID_SIWE_RESOURCE(
        type = "invalid_siwe_resource",
    ),
    INVALID_SIWE_STATEMENT(
        type = "invalid_siwe_statement",
    ),
    INVALID_SIWE_URI(
        type = "invalid_siwe_uri",
    ),
    INVALID_SOLANA_ADDRESS(
        type = "invalid_solana_address",
    ),
    INVALID_SSO_DEFAULT_CONNECTION_ID(
        type = "invalid_sso_default_connection_id",
    ),
    INVALID_SSO_IDP(
        type = "invalid_sso_idp",
    ),
    INVALID_STYTCH_PREFIXED_RESOURCE(
        type = "invalid_stytch_prefixed_resource",
    ),
    INVALID_SUBJECT(
        type = "invalid_subject",
    ),
    INVALID_TEMPLATE_ID(
        type = "invalid_template_id",
    ),
    INVALID_TEMPLATE_VALUES(
        type = "invalid_template_values",
    ),
    INVALID_TOKEN(
        type = "invalid_token",
    ),
    INVALID_TOKEN_DOCS(
        type = "invalid_token_docs",
    ),
    INVALID_TOKEN_URL(
        type = "invalid_token_url",
    ),
    INVALID_TOTP_CODE(
        type = "invalid_totp_code",
    ),
    INVALID_TOTP_ID(
        type = "invalid_totp_id",
    ),
    INVALID_URL(
        type = "invalid_url",
    ),
    INVALID_USER_AGENT(
        type = "invalid_user_agent",
    ),
    INVALID_USER_ID(
        type = "invalid_user_id",
    ),
    INVALID_USERINFO_URL(
        type = "invalid_userinfo_url",
    ),
    INVALID_WALLET_ADDRESS_USER(
        type = "invalid_wallet_address_user",
    ),
    INVALID_WALLET_TYPE(
        type = "invalid_wallet_type",
    ),
    INVALID_WEBAUTHN_REGISTRATION_DOMAIN(
        type = "invalid_webauthn_registration_domain",
    ),
    INVALID_WEBAUTHN_REGISTRATION_ID(
        type = "invalid_webauthn_registration_id",
    ),
    INVALID_WILDCARD_ACTION(
        type = "invalid_wildcard_action",
    ),
    INVALID_X509_CERTIFICATE(
        type = "invalid_x509_certificate",
    ),
    INVALID_XML_FROM_SAML_METADATA_URL(
        type = "invalid_xml_from_saml_metadata_url",
    ),
    INVALID_XML_SAML_RESPONSE(
        type = "invalid_xml_saml_response",
    ),
    JWT_INVALID_AUDIENCE(
        type = "jwt_invalid_audience",
    ),
    JWT_INVALID_CLAIMS(
        type = "jwt_invalid_claims",
    ),
    JWT_INVALID_ISSUER(
        type = "jwt_invalid_issuer",
    ),
    JWT_TEMPLATE_INVALID_JSON(
        type = "jwt_template_invalid_json",
    ),
    JWT_TEMPLATE_INVALID_TAG(
        type = "jwt_template_invalid_tag",
    ),
    JWT_TEMPLATE_MISMATCHED_TAG(
        type = "jwt_template_mismatched_tag",
    ),
    JWT_TEMPLATE_NOT_FOUND(
        type = "jwt_template_not_found",
    ),
    LIVE_ID_USED_IN_TEST_ENVIRONMENT(
        type = "live_id_used_in_test_environment",
    ),
    M2M_CLIENT_ALREADY_ROTATING_SECRET(
        type = "m2m_client_already_rotating_secret",
    ),
    M2M_CLIENT_INVALID_CLIENT_DESCRIPTION(
        type = "m2m_client_invalid_client_description",
    ),
    M2M_CLIENT_INVALID_CLIENT_ID(
        type = "m2m_client_invalid_client_id",
    ),
    M2M_CLIENT_INVALID_CLIENT_NAME(
        type = "m2m_client_invalid_client_name",
    ),
    M2M_CLIENT_INVALID_CLIENT_SECRET(
        type = "m2m_client_invalid_client_secret",
    ),
    M2M_CLIENT_INVALID_STATUS(
        type = "m2m_client_invalid_status",
    ),
    M2M_CLIENT_NOT_FOUND(
        type = "m2m_client_not_found",
    ),
    M2M_CLIENT_NOT_ROTATING_SECRET(
        type = "m2m_client_not_rotating_secret",
    ),
    M2M_SEARCH_EXPECTED_ARRAY_OF_STRINGS(
        type = "m2m_search_expected_array_of_strings",
    ),
    M2M_SEARCH_FILTER_NAME_MUST_BE_STRING(
        type = "m2m_search_filter_name_must_be_string",
    ),
    M2M_SEARCH_FILTER_NAME_NOT_RECOGNIZED(
        type = "m2m_search_filter_name_not_recognized",
    ),
    M2M_SEARCH_MISSING_FILTER_NAME(
        type = "m2m_search_missing_filter_name",
    ),
    M2M_SEARCH_MISSING_FILTER_VALUE(
        type = "m2m_search_missing_filter_value",
    ),
    MAGIC_LINK_NOT_FOUND(
        type = "magic_link_not_found",
    ),
    MEMBER_CANNOT_UPDATE_THEIR_EMAIL_ADDRESS(
        type = "member_cannot_update_their_email_address",
    ),
    MEMBER_DASHBOARD_SEARCH_DISABLED(
        type = "member_dashboard_search_disabled",
    ),
    MEMBER_DOES_NOT_EXIST_IN_ORGANIZATION(
        type = "member_does_not_exist_in_organization",
    ),
    MEMBER_IMPERSONATION_NOT_ALLOWED(
        type = "member_impersonation_not_allowed",
    ),
    MEMBER_NOT_FOUND(
        type = "member_not_found",
    ),
    MEMBER_OAUTH_TOKEN_NOT_FOUND(
        type = "member_oauth_token_not_found",
    ),
    MEMBER_OIDC_TOKEN_NOT_FOUND(
        type = "member_oidc_token_not_found",
    ),
    MEMBER_PASSWORD_NOT_FOUND(
        type = "member_password_not_found",
    ),
    MEMBER_PHONE_NUMBER_NOT_FOUND(
        type = "member_phone_number_not_found",
    ),
    MEMBER_RESET_PASSWORD(
        type = "member_reset_password",
    ),
    MEMBER_SEARCH_CANNOT_MIX_INTERNAL_AND_EXTERNAL_MEMBER_IDS(
        type = "member_search_cannot_mix_internal_and_external_member_ids",
    ),
    MEMBER_SEARCH_EXPECTED_ARRAY_OF_STRINGS(
        type = "member_search_expected_array_of_strings",
    ),
    MEMBER_SEARCH_EXPECTED_BOOLEAN(
        type = "member_search_expected_boolean",
    ),
    MEMBER_SEARCH_EXPECTED_STRING(
        type = "member_search_expected_string",
    ),
    MEMBER_SEARCH_FILTER_NAME_MUST_BE_STRING(
        type = "member_search_filter_name_must_be_string",
    ),
    MEMBER_SEARCH_FILTER_NAME_NOT_RECOGNIZED(
        type = "member_search_filter_name_not_recognized",
    ),
    MEMBER_SEARCH_MEMBER_EMAIL_FUZZY_TOO_SHORT(
        type = "member_search_member_email_fuzzy_too_short",
    ),
    MEMBER_SEARCH_MEMBER_MFA_PHONE_NUMBER_FUZZY_TOO_SHORT(
        type = "member_search_member_mfa_phone_number_fuzzy_too_short",
    ),
    MEMBER_SEARCH_MISSING_FILTER_NAME(
        type = "member_search_missing_filter_name",
    ),
    MEMBER_SEARCH_MISSING_FILTER_VALUE(
        type = "member_search_missing_filter_value",
    ),
    MEMBER_SEARCH_MISSING_IS_BREAKGLASS(
        type = "member_search_missing_is_breakglass",
    ),
    MEMBER_SEARCH_MISSING_MEMBER_EMAIL_FUZZY(
        type = "member_search_missing_member_email_fuzzy",
    ),
    MEMBER_SEARCH_MISSING_MEMBER_EMAILS(
        type = "member_search_missing_member_emails",
    ),
    MEMBER_SEARCH_MISSING_MEMBER_IDS(
        type = "member_search_missing_member_ids",
    ),
    MEMBER_SEARCH_MISSING_MEMBER_ROLES(
        type = "member_search_missing_member_roles",
    ),
    MEMBER_SEARCH_MISSING_MFA_MEMBER_PHONE_NUMBER_FUZZY(
        type = "member_search_missing_mfa_member_phone_number_fuzzy",
    ),
    MEMBER_SEARCH_MISSING_MFA_MEMBER_PHONE_NUMBERS(
        type = "member_search_missing_mfa_member_phone_numbers",
    ),
    MEMBER_SEARCH_MISSING_OAUTH_PROVIDERS(
        type = "member_search_missing_oauth_providers",
    ),
    MEMBER_SEARCH_MISSING_ORGANIZATION_ID(
        type = "member_search_missing_organization_id",
    ),
    MEMBER_SEARCH_MISSING_ORGANIZATION_SLUG(
        type = "member_search_missing_organization_slug",
    ),
    MEMBER_SEARCH_MISSING_ORGANIZATION_SLUG_FUZZY(
        type = "member_search_missing_organization_slug_fuzzy",
    ),
    MEMBER_SEARCH_MISSING_PASSWORD_EXISTS(
        type = "member_search_missing_password_exists",
    ),
    MEMBER_SEARCH_MISSING_STATUS(
        type = "member_search_missing_status",
    ),
    MEMBER_SEARCH_MISSING_STATUSES(
        type = "member_search_missing_statuses",
    ),
    MEMBER_SEARCH_MISSING_TOTP_EXISTS(
        type = "member_search_missing_totp_exists",
    ),
    MEMBER_SEARCH_ORGANIZATION_IDS_EMPTY(
        type = "member_search_organization_ids_empty",
    ),
    MEMBER_SEARCH_ORGANIZATION_SLUG_FUZZY_TOO_SHORT(
        type = "member_search_organization_slug_fuzzy_too_short",
    ),
    METADATA_INVALID_FORMAT(
        type = "metadata_invalid_format",
    ),
    METADATA_TOO_LARGE(
        type = "metadata_too_large",
    ),
    METADATA_TOO_MANY_KEYS(
        type = "metadata_too_many_keys",
    ),
    METHOD_NOT_ALLOWED(
        type = "method_not_allowed",
    ),
    MIGRATE_FROM_EXTERNAL_EMAIL_DOES_NOT_EXIST(
        type = "migrate_from_external_email_does_not_exist",
    ),
    MIGRATE_FROM_EXTERNAL_MISSING_USERINFO(
        type = "migrate_from_external_missing_userinfo",
    ),
    MIGRATE_FROM_EXTERNAL_UNEXPECTED_RESPONSE(
        type = "migrate_from_external_unexpected_response",
    ),
    MISSING_APPLE_APP_ID(
        type = "missing_apple_app_id",
    ),
    MISSING_OAUTH_ORGANIZATION_LOCATOR(
        type = "missing_oauth_organization_locator",
    ),
    MISSING_OAUTH_REFRESH_TOKEN(
        type = "missing_oauth_refresh_token",
    ),
    MISSING_OIDC_CSRF_COOKIE(
        type = "missing_oidc_csrf_cookie",
    ),
    MISSING_OIDC_REFRESH_TOKEN(
        type = "missing_oidc_refresh_token",
    ),
    MISSING_OIDC_STATE_PARAM(
        type = "missing_oidc_state_param",
    ),
    MISSING_SAML_CSRF_COOKIE(
        type = "missing_saml_csrf_cookie",
    ),
    MISSING_SAML_RELAY_STATE(
        type = "missing_saml_relay_state",
    ),
    MISSING_SAML_RESPONSE(
        type = "missing_saml_response",
    ),
    MISSING_SIGNATURE_SAML_RESPONSE(
        type = "missing_signature_saml_response",
    ),
    MISSING_SMS_PARAMETER(
        type = "missing_sms_parameter",
    ),
    MISSING_SSO_CONNECTION_LOCATOR(
        type = "missing_sso_connection_locator",
    ),
    NO_ACTIVE_BIOMETRIC_REGISTRATIONS(
        type = "no_active_biometric_registrations",
    ),
    NO_ACTIVE_RECOVERY_CODE_BACKED_FACTOR(
        type = "no_active_recovery_code_backed_factor",
    ),
    NO_ACTIVE_SCIM_CONNECTION(
        type = "no_active_scim_connection",
    ),
    NO_ACTIVE_WEBAUTHN_REGISTRATIONS(
        type = "no_active_webauthn_registrations",
    ),
    NO_ASSOCIATED_SESSION_PASSED_IN(
        type = "no_associated_session_passed_in",
    ),
    NO_CAPTCHA_PROVIDER_CONFIGURED(
        type = "no_captcha_provider_configured",
    ),
    NO_DEFAULT_DISCOVERY_REDIRECT_URL_SET(
        type = "no_default_discovery_redirect_url_set",
    ),
    NO_DEFAULT_INVITE_REDIRECT_URL_SET(
        type = "no_default_invite_redirect_url_set",
    ),
    NO_DEFAULT_LOGIN_REDIRECT_URL_SET(
        type = "no_default_login_redirect_url_set",
    ),
    NO_DEFAULT_SIGNUP_REDIRECT_URL_SET(
        type = "no_default_signup_redirect_url_set",
    ),
    NO_DELETED_MEMBER_FOUND_FOR_REACTIVATION(
        type = "no_deleted_member_found_for_reactivation",
    ),
    NO_DISCOVERY_REDIRECT_URL(
        type = "no_discovery_redirect_url",
    ),
    NO_DISCOVERY_REDIRECT_URLS_SET(
        type = "no_discovery_redirect_urls_set",
    ),
    NO_INVITE_REDIRECT_URL(
        type = "no_invite_redirect_url",
    ),
    NO_INVITE_REDIRECT_URLS_SET(
        type = "no_invite_redirect_urls_set",
    ),
    NO_LOGIN_REDIRECT_URL(
        type = "no_login_redirect_url",
    ),
    NO_LOGIN_REDIRECT_URLS_SET(
        type = "no_login_redirect_urls_set",
    ),
    NO_MATCH_FOR_PROVIDED_MAGIC_LINK_URL(
        type = "no_match_for_provided_magic_link_url",
    ),
    NO_MATCH_FOR_PROVIDED_OAUTH_URL(
        type = "no_match_for_provided_oauth_url",
    ),
    NO_MATCH_FOR_PROVIDED_SSO_URL(
        type = "no_match_for_provided_sso_url",
    ),
    NO_PASSWORD_RESET_REDIRECT_URL(
        type = "no_password_reset_redirect_url",
    ),
    NO_PENDING_WEBAUTHN_REGISTRATION(
        type = "no_pending_webauthn_registration",
    ),
    NO_SESSION_ARGUMENTS(
        type = "no_session_arguments",
    ),
    NO_SESSION_REVOKE_ARGUMENTS(
        type = "no_session_revoke_arguments",
    ),
    NO_SIGNUP_REDIRECT_URL(
        type = "no_signup_redirect_url",
    ),
    NO_SIGNUP_REDIRECT_URLS_SET(
        type = "no_signup_redirect_urls_set",
    ),
    NO_SSO_CONNECTION_EXISTS_FOR_ORGANIZATION(
        type = "no_sso_connection_exists_for_organization",
    ),
    NO_USER_PASSWORD(
        type = "no_user_password",
    ),
    NO_USER_SELECTION_ARGUMENTS(
        type = "no_user_selection_arguments",
    ),
    NO_WILDCARDS_IN_LIVE_REDIRECT_URL(
        type = "no_wildcards_in_live_redirect_url",
    ),
    NOT_YET_VALID_SAML_RESPONSE(
        type = "not_yet_valid_saml_response",
    ),
    OAUTH_ACCESS_TOKEN_EXCHANGE_MISSING_FULL_ACCESS(
        type = "oauth_access_token_exchange_missing_full_access",
    ),
    OAUTH_ACCESS_TOKEN_EXCHANGE_TOKEN_INVALID_GRANT(
        type = "oauth_access_token_exchange_token_invalid_grant",
    ),
    OAUTH_ACCESS_TOKEN_EXCHANGE_TOKEN_TOO_OLD(
        type = "oauth_access_token_exchange_token_too_old",
    ),
    OAUTH_APP_NOT_AUTHORIZED(
        type = "oauth_app_not_authorized",
    ),
    OAUTH_AUTH_CODE_ERROR(
        type = "oauth_auth_code_error",
    ),
    OAUTH_CONFIG_NOT_FOUND(
        type = "oauth_config_not_found",
    ),
    OAUTH_CREATING_REDIRECT_URL_ERROR(
        type = "oauth_creating_redirect_url_error",
    ),
    OAUTH_DISCOVERY_FLOW_CALLBACK_ERROR(
        type = "oauth_discovery_flow_callback_error",
    ),
    OAUTH_FLOW_CALLBACK_ERROR(
        type = "oauth_flow_callback_error",
    ),
    OAUTH_INVALID_CALLBACK_REQUEST(
        type = "oauth_invalid_callback_request",
    ),
    OAUTH_INVALID_STATE(
        type = "oauth_invalid_state",
    ),
    OAUTH_NON_DISCOVERY_FLOW_CALLBACK_ERROR(
        type = "oauth_non_discovery_flow_callback_error",
    ),
    OAUTH_STATE_MISMATCH(
        type = "oauth_state_mismatch",
    ),
    OAUTH_STATE_USED(
        type = "oauth_state_used",
    ),
    OAUTH_TENANT_JIT_PROVISIONING_NOT_ALLOWED(
        type = "oauth_tenant_jit_provisioning_not_allowed",
    ),
    OAUTH_TOKEN_EXCHANGE_GOOGLE_MISSING_INFORMATION(
        type = "oauth_token_exchange_google_missing_information",
    ),
    OAUTH_TOKEN_EXCHANGE_INVALID_CLIENT(
        type = "oauth_token_exchange_invalid_client",
    ),
    OAUTH_TOKEN_EXCHANGE_INVALID_REQUEST(
        type = "oauth_token_exchange_invalid_request",
    ),
    OAUTH_TOKEN_EXCHANGE_INVALID_SCOPE(
        type = "oauth_token_exchange_invalid_scope",
    ),
    OAUTH_TOKEN_EXCHANGE_MICROSOFT_EXPIRED_SECRET(
        type = "oauth_token_exchange_microsoft_expired_secret",
    ),
    OAUTH_TOKEN_EXCHANGE_MICROSOFT_INVALID_SECRET(
        type = "oauth_token_exchange_microsoft_invalid_secret",
    ),
    OAUTH_TOKEN_EXCHANGE_UNAUTHORIZED_CLIENT(
        type = "oauth_token_exchange_unauthorized_client",
    ),
    OAUTH_TOKEN_NOT_FOUND(
        type = "oauth_token_not_found",
    ),
    OAUTH_UNVERIFIED_EMAIL(
        type = "oauth_unverified_email",
    ),
    OAUTH_USER_REGISTRATION_NOT_FOUND(
        type = "oauth_user_registration_not_found",
    ),
    OIDC_AUTH_CODE_ERROR(
        type = "oidc_auth_code_error",
    ),
    OIDC_CONNECTION_AUTHORIZATION_URL_INVALID_FORMAT(
        type = "oidc_connection_authorization_url_invalid_format",
    ),
    OIDC_CONNECTION_AUTHORIZATION_URL_REQUIRED(
        type = "oidc_connection_authorization_url_required",
    ),
    OIDC_CONNECTION_CLIENT_ID_REQUIRED(
        type = "oidc_connection_client_id_required",
    ),
    OIDC_CONNECTION_CLIENT_SECRET_REQUIRED(
        type = "oidc_connection_client_secret_required",
    ),
    OIDC_CONNECTION_ISSUER_REQUIRED(
        type = "oidc_connection_issuer_required",
    ),
    OIDC_CONNECTION_ISSUER_URL_INVALID_FORMAT(
        type = "oidc_connection_issuer_url_invalid_format",
    ),
    OIDC_CONNECTION_JWKS_URL_INVALID_FORMAT(
        type = "oidc_connection_jwks_url_invalid_format",
    ),
    OIDC_CONNECTION_JWKS_URL_REQUIRED(
        type = "oidc_connection_jwks_url_required",
    ),
    OIDC_CONNECTION_NOT_FOUND(
        type = "oidc_connection_not_found",
    ),
    OIDC_CONNECTION_TOKEN_URL_INVALID_FORMAT(
        type = "oidc_connection_token_url_invalid_format",
    ),
    OIDC_CONNECTION_TOKEN_URL_REQUIRED(
        type = "oidc_connection_token_url_required",
    ),
    OIDC_CONNECTION_USERINFO_URL_INVALID_FORMAT(
        type = "oidc_connection_userinfo_url_invalid_format",
    ),
    OIDC_CONNECTION_USERINFO_URL_REQUIRED(
        type = "oidc_connection_userinfo_url_required",
    ),
    OIDC_CSRF_COOKIE_MISMATCH(
        type = "oidc_csrf_cookie_mismatch",
    ),
    OIDC_INVALID_CALLBACK_REQUEST(
        type = "oidc_invalid_callback_request",
    ),
    OIDC_INVALID_USERINFO(
        type = "oidc_invalid_userinfo",
    ),
    OIDC_RESPONSE_EMAIL_MISSING(
        type = "oidc_response_email_missing",
    ),
    OIDC_RESPONSE_NAME_MISSING(
        type = "oidc_response_name_missing",
    ),
    OIDC_RESPONSE_SUBJECT_MISMATCH(
        type = "oidc_response_subject_mismatch",
    ),
    OIDC_RESPONSE_SUBJECT_MISSING(
        type = "oidc_response_subject_missing",
    ),
    OIDC_RESPONSE_UNVERIFIED_EMAIL(
        type = "oidc_response_unverified_email",
    ),
    OIDC_USER_NOT_ASSIGNED_TO_APP_IN_IDP(
        type = "oidc_user_not_assigned_to_app_in_idp",
    ),
    OPERATION_RESTRICTED_BY_ORGANIZATION_AUTH_METHODS(
        type = "operation_restricted_by_organization_auth_methods",
    ),
    OPERATION_RESTRICTED_BY_ORGANIZATION_MFA_METHODS(
        type = "operation_restricted_by_organization_mfa_methods",
    ),
    ORGANIZATION_LOGO_URL_TOO_LONG(
        type = "organization_logo_url_too_long",
    ),
    ORGANIZATION_NAME_INCLUDES_BANNED_WORDS(
        type = "organization_name_includes_banned_words",
    ),
    ORGANIZATION_NAME_MISSING(
        type = "organization_name_missing",
    ),
    ORGANIZATION_NOT_FOUND(
        type = "organization_not_found",
    ),
    ORGANIZATION_SEARCH_ALLOWED_DOMAIN_FUZZY_TOO_SHORT(
        type = "organization_search_allowed_domain_fuzzy_too_short",
    ),
    ORGANIZATION_SEARCH_EXPECTED_ARRAY_OF_STRINGS(
        type = "organization_search_expected_array_of_strings",
    ),
    ORGANIZATION_SEARCH_EXPECTED_STRING(
        type = "organization_search_expected_string",
    ),
    ORGANIZATION_SEARCH_FILTER_NAME_MUST_BE_STRING(
        type = "organization_search_filter_name_must_be_string",
    ),
    ORGANIZATION_SEARCH_FILTER_NAME_NOT_RECOGNIZED(
        type = "organization_search_filter_name_not_recognized",
    ),
    ORGANIZATION_SEARCH_MEMBER_EMAIL_FUZZY_TOO_SHORT(
        type = "organization_search_member_email_fuzzy_too_short",
    ),
    ORGANIZATION_SEARCH_MISSING_ALLOWED_DOMAIN_FUZZY(
        type = "organization_search_missing_allowed_domain_fuzzy",
    ),
    ORGANIZATION_SEARCH_MISSING_ALLOWED_DOMAINS(
        type = "organization_search_missing_allowed_domains",
    ),
    ORGANIZATION_SEARCH_MISSING_CLAIMED_EMAIL_DOMAINS(
        type = "organization_search_missing_claimed_email_domains",
    ),
    ORGANIZATION_SEARCH_MISSING_FILTER_NAME(
        type = "organization_search_missing_filter_name",
    ),
    ORGANIZATION_SEARCH_MISSING_FILTER_VALUE(
        type = "organization_search_missing_filter_value",
    ),
    ORGANIZATION_SEARCH_MISSING_MEMBER_EMAIL_FUZZY(
        type = "organization_search_missing_member_email_fuzzy",
    ),
    ORGANIZATION_SEARCH_MISSING_MEMBER_EMAILS(
        type = "organization_search_missing_member_emails",
    ),
    ORGANIZATION_SEARCH_MISSING_ORGANIZATION_IDS(
        type = "organization_search_missing_organization_ids",
    ),
    ORGANIZATION_SEARCH_MISSING_ORGANIZATION_NAME_FUZZY(
        type = "organization_search_missing_organization_name_fuzzy",
    ),
    ORGANIZATION_SEARCH_MISSING_ORGANIZATION_SLUG_FUZZY(
        type = "organization_search_missing_organization_slug_fuzzy",
    ),
    ORGANIZATION_SEARCH_MISSING_ORGANIZATION_SLUGS(
        type = "organization_search_missing_organization_slugs",
    ),
    ORGANIZATION_SEARCH_ORGANIZATION_NAME_FUZZY_TOO_SHORT(
        type = "organization_search_organization_name_fuzzy_too_short",
    ),
    ORGANIZATION_SEARCH_ORGANIZATION_SLUG_FUZZY_TOO_SHORT(
        type = "organization_search_organization_slug_fuzzy_too_short",
    ),
    ORGANIZATION_SETTINGS_CLAIMED_DOMAIN_TOO_COMMON(
        type = "organization_settings_claimed_domain_too_common",
    ),
    ORGANIZATION_SETTINGS_DISPOSABLE_DOMAIN(
        type = "organization_settings_disposable_domain",
    ),
    ORGANIZATION_SETTINGS_DOMAIN_TOO_COMMON(
        type = "organization_settings_domain_too_common",
    ),
    ORGANIZATION_SETTINGS_DUPLICATE_CLAIMED_DOMAIN(
        type = "organization_settings_duplicate_claimed_domain",
    ),
    ORGANIZATION_SETTINGS_DUPLICATE_DOMAIN(
        type = "organization_settings_duplicate_domain",
    ),
    ORGANIZATION_SETTINGS_INVALID_CLAIMED_DOMAIN(
        type = "organization_settings_invalid_claimed_domain",
    ),
    ORGANIZATION_SETTINGS_INVALID_DOMAIN(
        type = "organization_settings_invalid_domain",
    ),
    ORGANIZATION_SLUG_ALREADY_USED(
        type = "organization_slug_already_used",
    ),
    ORGANIZATION_SUSPENDED(
        type = "organization_suspended",
    ),
    ORGANIZATION_USER_NOT_FOUND(
        type = "organization_user_not_found",
    ),
    OTP_CODE_NOT_FOUND(
        type = "otp_code_not_found",
    ),
    PASSWORD_ALREADY_EXISTS(
        type = "password_already_exists",
    ),
    PASSWORD_DOES_NOT_MATCH(
        type = "password_does_not_match",
    ),
    PASSWORD_NOT_FOUND(
        type = "password_not_found",
    ),
    PASSWORD_VALIDATION_TIMEOUT(
        type = "password_validation_timeout",
    ),
    PASSWORDS_INCOMPATIBLE_WITH_SDK_CONFIG(
        type = "passwords_incompatible_with_sdk_config",
    ),
    PBKDF_2_KEY_LENGTH_MISMATCH(
        type = "pbkdf_2_key_length_mismatch",
    ),
    PENDING_TOTP_EXISTS(
        type = "pending_totp_exists",
    ),
    PHONE_NUMBER_NOT_FOUND(
        type = "phone_number_not_found",
    ),
    PKCE_DID_NOT_EXPECT_CODE_VERIFIER(
        type = "pkce_did_not_expect_code_verifier",
    ),
    PKCE_EXPECTED_CODE_VERIFIER(
        type = "pkce_expected_code_verifier",
    ),
    PKCE_MISMATCH(
        type = "pkce_mismatch",
    ),
    PKCE_REQUIRED_FOR_IDP_AUTHORIZATION_FLOW(
        type = "pkce_required_for_idp_authorization_flow",
    ),
    PKCE_REQUIRED_FOR_NATIVE_CALLBACK(
        type = "pkce_required_for_native_callback",
    ),
    PRIVATE_KEY_TOO_LONG(
        type = "private_key_too_long",
    ),
    PROJECT_HAS_NO_PUBLIC_TOKENS(
        type = "project_has_no_public_tokens",
    ),
    PROJECT_NOT_FOUND(
        type = "project_not_found",
    ),
    PROJECT_USER_NOT_FOUND(
        type = "project_user_not_found",
    ),
    PUBLIC_IDP_CLIENTS_DO_NOT_HAVE_SECRETS(
        type = "public_idp_clients_do_not_have_secrets",
    ),
    PUBLIC_KEY_MISSING(
        type = "public_key_missing",
    ),
    PUBLIC_TOKEN_NOT_FOUND(
        type = "public_token_not_found",
    ),
    PUBLIC_TOKEN_REQUIRED(
        type = "public_token_required",
    ),
    QUERY_PARAMS_DO_NOT_MATCH(
        type = "query_params_do_not_match",
    ),
    RBAC_ACTION_DUPLICATE(
        type = "rbac_action_duplicate",
    ),
    RBAC_ACTION_TOO_LONG(
        type = "rbac_action_too_long",
    ),
    RBAC_DESCRIPTION_TOO_LONG(
        type = "rbac_description_too_long",
    ),
    RBAC_DOMAIN_TOO_COMMON(
        type = "rbac_domain_too_common",
    ),
    RBAC_INVALID_DOMAIN(
        type = "rbac_invalid_domain",
    ),
    RBAC_PERMISSION_MISSING_ACTIONS(
        type = "rbac_permission_missing_actions",
    ),
    RBAC_RESERVED_SCOPE(
        type = "rbac_reserved_scope",
    ),
    RBAC_RESOURCE_DUPLICATE(
        type = "rbac_resource_duplicate",
    ),
    RBAC_RESOURCE_ID_TOO_LONG(
        type = "rbac_resource_id_too_long",
    ),
    RBAC_RESOURCE_MISSING_ACTIONS(
        type = "rbac_resource_missing_actions",
    ),
    RBAC_ROLE_DUPLICATE(
        type = "rbac_role_duplicate",
    ),
    RBAC_ROLE_ID_TOO_LONG(
        type = "rbac_role_id_too_long",
    ),
    RBAC_ROLE_MISSING_PERMISSIONS(
        type = "rbac_role_missing_permissions",
    ),
    RBAC_SCOPE_DUPLICATE(
        type = "rbac_scope_duplicate",
    ),
    RBAC_SCOPE_MISSING_PERMISSIONS(
        type = "rbac_scope_missing_permissions",
    ),
    RBAC_SCOPE_TOO_LONG(
        type = "rbac_scope_too_long",
    ),
    REACTIVATION_EMAIL_BELONGS_TO_ANOTHER_MEMBER(
        type = "reactivation_email_belongs_to_another_member",
    ),
    RECOVERY_CODE_USED_AS_TOTP_CODE(
        type = "recovery_code_used_as_totp_code",
    ),
    RECOVERY_CODES_ALREADY_EXIST(
        type = "recovery_codes_already_exist",
    ),
    RECOVERY_CODES_NOT_FOUND(
        type = "recovery_codes_not_found",
    ),
    REDIRECT_URL_CANNOT_USE_PROTOCOL(
        type = "redirect_url_cannot_use_protocol",
    ),
    REDIRECT_URL_MUST_USE_HTTPS(
        type = "redirect_url_must_use_https",
    ),
    REDIRECT_URL_NOT_FOUND(
        type = "redirect_url_not_found",
    ),
    REDIRECT_URL_WITH_QUERY_PARAM_PLACEHOLDER_CANNOT_BE_DEFAULT(
        type = "redirect_url_with_query_param_placeholder_cannot_be_default",
    ),
    REDIRECT_URL_WITH_WILDCARD_CANNOT_BE_DEFAULT(
        type = "redirect_url_with_wildcard_cannot_be_default",
    ),
    REQUIRED_CUSTOM_EMAIL_DOMAIN(
        type = "required_custom_email_domain",
    ),
    REQUIRES_ACTIVE_SSO_CONNECTION(
        type = "requires_active_sso_connection",
    ),
    RESERVED_CLAIMS_IN_CUSTOM_CLAIMS(
        type = "reserved_claims_in_custom_claims",
    ),
    RESET_PASSWORD(
        type = "reset_password",
    ),
    RETIRED_EMAIL_MISSING_EMAIL_IDENTIFIERS(
        type = "retired_email_missing_email_identifiers",
    ),
    RETIRED_MEMBER_EMAIL(
        type = "retired_member_email",
    ),
    ROUTE_NOT_FOUND(
        type = "route_not_found",
    ),
    ROUTE_NOT_SUPPORTED_IN_TEST(
        type = "route_not_supported_in_test",
    ),
    SAML_CERTIFICATE_MISMATCH(
        type = "saml_certificate_mismatch",
    ),
    SAML_CONNECTION_ATTRIBUTE_MAPPING_MISSING_GROUPS_KEY(
        type = "saml_connection_attribute_mapping_missing_groups_key",
    ),
    SAML_CONNECTION_ATTRIBUTE_MAPPING_REQUIRED(
        type = "saml_connection_attribute_mapping_required",
    ),
    SAML_CONNECTION_EMAIL_MAPPING_REQUIRED(
        type = "saml_connection_email_mapping_required",
    ),
    SAML_CONNECTION_IDP_SSO_URL_INVALID_FORMAT(
        type = "saml_connection_idp_sso_url_invalid_format",
    ),
    SAML_CONNECTION_IDP_SSO_URL_NOT_HTTPS(
        type = "saml_connection_idp_sso_url_not_https",
    ),
    SAML_CONNECTION_IDP_SSO_URL_REQUIRED(
        type = "saml_connection_idp_sso_url_required",
    ),
    SAML_CONNECTION_METADATA_INVALID_FORMAT(
        type = "saml_connection_metadata_invalid_format",
    ),
    SAML_CONNECTION_NAME_MAPPING_REQUIRED(
        type = "saml_connection_name_mapping_required",
    ),
    SAML_CONNECTION_NO_NESTED_KEYS_IN_ATTRIBUTE_MAPPING(
        type = "saml_connection_no_nested_keys_in_attribute_mapping",
    ),
    SAML_CONNECTION_NOT_FOUND(
        type = "saml_connection_not_found",
    ),
    SAML_CSRF_COOKIE_MISMATCH(
        type = "saml_csrf_cookie_mismatch",
    ),
    SAML_GROUP_ROLE_ASSIGNMENT_DOES_NOT_CONTAIN_MEMBER_GROUP(
        type = "saml_group_role_assignment_does_not_contain_member_group",
    ),
    SAML_SIGNING_PRIVATE_KEY_FORMAT_NOT_SUPPORTED(
        type = "saml_signing_private_key_format_not_supported",
    ),
    SAML_SIGNING_PRIVATE_KEY_INVALID(
        type = "saml_signing_private_key_invalid",
    ),
    SCIM_CONNECTION_NOT_ROTATING_TOKEN(
        type = "scim_connection_not_rotating_token",
    ),
    SCIM_GROUP_ROLE_ASSIGNMENT_DOES_NOT_CONTAIN_GROUP_ID(
        type = "scim_group_role_assignment_does_not_contain_group_id",
    ),
    SCIM_GROUP_ROLE_ASSIGNMENT_GROUP_DOES_NOT_EXIST(
        type = "scim_group_role_assignment_group_does_not_exist",
    ),
    SCRYPT_KEY_LENGTH_MISMATCH(
        type = "scrypt_key_length_mismatch",
    ),
    SEARCH_INVALID_STATUS_FILTER(
        type = "search_invalid_status_filter",
    ),
    SEARCH_TIMEOUT(
        type = "search_timeout",
    ),
    SECONDARY_FACTOR_NOT_FOUND(
        type = "secondary_factor_not_found",
    ),
    SECRET_NOT_FOUND(
        type = "secret_not_found",
    ),
    SERVER_UNAVAILABLE(
        type = "server_unavailable",
    ),
    SESSION_AUTHORIZATION_ERROR(
        type = "session_authorization_error",
    ),
    SESSION_AUTHORIZATION_TENANCY_ERROR(
        type = "session_authorization_tenancy_error",
    ),
    SESSION_ENFORCED_FACTOR_MATCHING_SESSION_REQUIRED(
        type = "session_enforced_factor_matching_session_required",
    ),
    SESSION_MUST_HAVE_AT_LEAST_ONE_ACTIVE_FACTOR(
        type = "session_must_have_at_least_one_active_factor",
    ),
    SESSION_NOT_FOUND(
        type = "session_not_found",
    ),
    SESSION_NOT_MUTABLE_IMPERSONATED(
        type = "session_not_mutable_impersonated",
    ),
    SESSION_ON_CODE_DOES_NOT_MATCH_SESSION_PASSED_IN(
        type = "session_on_code_does_not_match_session_passed_in",
    ),
    SESSION_TOO_OLD_TO_RESET_PASSWORD(
        type = "session_too_old_to_reset_password",
    ),
    SIGNATURE_IS_INCORRECT_SIZE(
        type = "signature_is_incorrect_size",
    ),
    SIGNATURE_MISSING(
        type = "signature_missing",
    ),
    SIWE_MESSAGE_EXPIRED(
        type = "siwe_message_expired",
    ),
    SIWE_MESSAGE_NOT_VALID_YET(
        type = "siwe_message_not_valid_yet",
    ),
    SMS_TEMPLATE_NOT_FOUND(
        type = "sms_template_not_found",
    ),
    SSO_CONNECTION_NOT_FOUND(
        type = "sso_connection_not_found",
    ),
    SSO_CONNECTION_ORGANIZATION_MISMATCH(
        type = "sso_connection_organization_mismatch",
    ),
    SSO_DISCOVERY_INCOMPATIBLE_WITH_SDK_CONFIG(
        type = "sso_discovery_incompatible_with_sdk_config",
    ),
    SSO_TOKEN_NOT_FOUND(
        type = "sso_token_not_found",
    ),
    SSO_VERIFICATION_KEY_NOT_FOUND(
        type = "sso_verification_key_not_found",
    ),
    STALE_FACTORS(
        type = "stale_factors",
    ),
    STYTCH_CLAIMS_IN_CUSTOM_CLAIMS(
        type = "stytch_claims_in_custom_claims",
    ),
    TEST_ID_USED_IN_LIVE_ENVIRONMENT(
        type = "test_id_used_in_live_environment",
    ),
    THIRD_PARTY_CLIENTS_CANNOT_USE_FULL_ACCESS(
        type = "third_party_clients_cannot_use_full_access",
    ),
    TOO_FEW_TOKENS_AUTHENTICATED(
        type = "too_few_tokens_authenticated",
    ),
    TOO_MANY_BIOMETRIC_REGISTRATIONS_FOR_USER(
        type = "too_many_biometric_registrations_for_user",
    ),
    TOO_MANY_DOMAINS(
        type = "too_many_domains",
    ),
    TOO_MANY_EMAIL_TEMPLATES(
        type = "too_many_email_templates",
    ),
    TOO_MANY_IDP_CLIENTS(
        type = "too_many_idp_clients",
    ),
    TOO_MANY_M2M_CLIENT_SCOPES(
        type = "too_many_m2m_client_scopes",
    ),
    TOO_MANY_M2M_CLIENTS_FOR_BILLING_TIER(
        type = "too_many_m2m_clients_for_billing_tier",
    ),
    TOO_MANY_OAUTH_ORGANIZATION_LOCATORS(
        type = "too_many_oauth_organization_locators",
    ),
    TOO_MANY_PROJECTS(
        type = "too_many_projects",
    ),
    TOO_MANY_PUBLIC_TOKENS(
        type = "too_many_public_tokens",
    ),
    TOO_MANY_REDIRECT_URLS(
        type = "too_many_redirect_urls",
    ),
    TOO_MANY_REQUESTS(
        type = "too_many_requests",
    ),
    TOO_MANY_ROLES(
        type = "too_many_roles",
    ),
    TOO_MANY_SECRETS(
        type = "too_many_secrets",
    ),
    TOO_MANY_SESSION_ARGUMENTS(
        type = "too_many_session_arguments",
    ),
    TOO_MANY_SESSION_REVOKE_ARGUMENTS(
        type = "too_many_session_revoke_arguments",
    ),
    TOO_MANY_SMS_TEMPLATES(
        type = "too_many_sms_templates",
    ),
    TOO_MANY_SSO_CONNECTION_LOCATORS(
        type = "too_many_sso_connection_locators",
    ),
    TOO_MANY_SSO_CONNECTIONS(
        type = "too_many_sso_connections",
    ),
    TOO_MANY_SSO_CONNECTIONS_FOR_BILLING_TIER(
        type = "too_many_sso_connections_for_billing_tier",
    ),
    TOO_MANY_SSO_VERIFICATION_CERTIFICATES(
        type = "too_many_sso_verification_certificates",
    ),
    TOO_MANY_UNVERIFIED_FACTORS(
        type = "too_many_unverified_factors",
    ),
    TOO_MANY_USER_REGISTRATIONS_FOR_DOMAIN(
        type = "too_many_user_registrations_for_domain",
    ),
    TOO_MANY_USER_SELECTION_ARGUMENTS(
        type = "too_many_user_selection_arguments",
    ),
    TOTP_CODE_ALREADY_AUTHENTICATED(
        type = "totp_code_already_authenticated",
    ),
    TOTP_CODE_USED_AS_RECOVERY_CODE(
        type = "totp_code_used_as_recovery_code",
    ),
    TOTP_NOT_FOUND(
        type = "totp_not_found",
    ),
    TOTPS_NOT_FOUND_FOR_USER(
        type = "totps_not_found_for_user",
    ),
    TWITTER_401(
        type = "twitter_401",
    ),
    TWITTER_403(
        type = "twitter_403",
    ),
    UNABLE_TO_AUTH_BIOMETRIC_REGISTRATION(
        type = "unable_to_auth_biometric_registration",
    ),
    UNABLE_TO_AUTH_IMPERSONATION_TOKEN(
        type = "unable_to_auth_impersonation_token",
    ),
    UNABLE_TO_AUTH_MAGIC_LINK(
        type = "unable_to_auth_magic_link",
    ),
    UNABLE_TO_AUTH_OTP_CODE(
        type = "unable_to_auth_otp_code",
    ),
    UNABLE_TO_AUTH_PASSWORD_RESET_TOKEN(
        type = "unable_to_auth_password_reset_token",
    ),
    UNABLE_TO_AUTH_WEBAUTHN_REGISTRATION(
        type = "unable_to_auth_webauthn_registration",
    ),
    UNABLE_TO_AUTHENTICATE_CRYPTO_WALLET(
        type = "unable_to_authenticate_crypto_wallet",
    ),
    UNABLE_TO_AUTHENTICATE_RECOVERY_CODE(
        type = "unable_to_authenticate_recovery_code",
    ),
    UNABLE_TO_AUTHENTICATE_TOTP(
        type = "unable_to_authenticate_totp",
    ),
    UNABLE_TO_AUTHORIZE_OAUTH_PROVIDER(
        type = "unable_to_authorize_oauth_provider",
    ),
    UNABLE_TO_DELETE_EMAIL_SUPPRESSION(
        type = "unable_to_delete_email_suppression",
    ),
    UNABLE_TO_DELETE_LAST_PUBLIC_TOKEN(
        type = "unable_to_delete_last_public_token",
    ),
    UNABLE_TO_PARSE_SESSION_JWT(
        type = "unable_to_parse_session_jwt",
    ),
    UNABLE_TO_REFRESH_OAUTH_TOKEN(
        type = "unable_to_refresh_oauth_token",
    ),
    UNABLE_TO_REFRESH_OIDC_TOKEN(
        type = "unable_to_refresh_oidc_token",
    ),
    UNABLE_TO_REGISTER_BIOMETRIC_REGISTRATION(
        type = "unable_to_register_biometric_registration",
    ),
    UNABLE_TO_REGISTER_WEBAUTHN_REGISTRATION(
        type = "unable_to_register_webauthn_registration",
    ),
    UNABLE_TO_REPRESENT_CUSTOM_CLAIMS_JSON(
        type = "unable_to_represent_custom_claims_json",
    ),
    UNAUTHORIZED_ACTION(
        type = "unauthorized_action",
    ),
    UNAUTHORIZED_BILLING_PERMISSIONS(
        type = "unauthorized_billing_permissions",
    ),
    UNAUTHORIZED_CREDENTIALS(
        type = "unauthorized_credentials",
    ),
    UNAUTHORIZED_CREDENTIALS_HOMEPAGE(
        type = "unauthorized_credentials_homepage",
    ),
    UNAUTHORIZED_PROJECT_ID_LIVE(
        type = "unauthorized_project_id_live",
    ),
    UNAUTHORIZED_PROJECT_ID_TEST(
        type = "unauthorized_project_id_test",
    ),
    UNAVAILABLE_ENDPOINT(
        type = "unavailable_endpoint",
    ),
    UNKNOWN_ERROR(
        type = "unknown_error",
    ),
    UNSOLICITED_OIDC_RESPONSE(
        type = "unsolicited_oidc_response",
    ),
    UNSOLICITED_SAML_RESPONSE(
        type = "unsolicited_saml_response",
    ),
    UNSUBSCRIBED_PHONE_NUMBER(
        type = "unsubscribed_phone_number",
    ),
    UPDATE_USER_AUTH_METHOD_NOT_ALLOWED(
        type = "update_user_auth_method_not_allowed",
    ),
    USE_HTTPS(
        type = "use_https",
    ),
    USER_ALREADY_INVITED(
        type = "user_already_invited",
    ),
    USER_ID_MISMATCH(
        type = "user_id_mismatch",
    ),
    USER_IMPERSONATION_NOT_ALLOWED(
        type = "user_impersonation_not_allowed",
    ),
    USER_NOT_FOUND(
        type = "user_not_found",
    ),
    USER_SEARCH_CANNOT_MIX_INTERNAL_AND_EXTERNAL_USER_IDS(
        type = "user_search_cannot_mix_internal_and_external_user_ids",
    ),
    USER_SEARCH_EMAIL_ADDRESS_FUZZY_TOO_SHORT(
        type = "user_search_email_address_fuzzy_too_short",
    ),
    USER_SEARCH_EXPECTED_ARRAY_OF_STRING(
        type = "user_search_expected_array_of_string",
    ),
    USER_SEARCH_EXPECTED_BOOL(
        type = "user_search_expected_bool",
    ),
    USER_SEARCH_EXPECTED_OBJECT(
        type = "user_search_expected_object",
    ),
    USER_SEARCH_EXPECTED_STRING(
        type = "user_search_expected_string",
    ),
    USER_SEARCH_EXPECTED_TIMESTAMP(
        type = "user_search_expected_timestamp",
    ),
    USER_SEARCH_FILTER_NAME_MUST_BE_STRING(
        type = "user_search_filter_name_must_be_string",
    ),
    USER_SEARCH_FILTER_NAME_NOT_RECOGNIZED(
        type = "user_search_filter_name_not_recognized",
    ),
    USER_SEARCH_FULL_NAME_FUZZY_TOO_SHORT(
        type = "user_search_full_name_fuzzy_too_short",
    ),
    USER_SEARCH_INVALID_CURSOR(
        type = "user_search_invalid_cursor",
    ),
    USER_SEARCH_INVALID_LIMIT(
        type = "user_search_invalid_limit",
    ),
    USER_SEARCH_INVALID_OAUTH_PROVIDER_FILTER(
        type = "user_search_invalid_oauth_provider_filter",
    ),
    USER_SEARCH_INVALID_OPERATOR(
        type = "user_search_invalid_operator",
    ),
    USER_SEARCH_INVALID_STATUS_FILTER(
        type = "user_search_invalid_status_filter",
    ),
    USER_SEARCH_MAXIMUM_FILTER_VALUE_COUNT_EXCEEDED(
        type = "user_search_maximum_filter_value_count_exceeded",
    ),
    USER_SEARCH_MISSING_BIOMETRIC_REGISTRATION_IDS(
        type = "user_search_missing_biometric_registration_ids",
    ),
    USER_SEARCH_MISSING_BIOMETRIC_VERIFIED(
        type = "user_search_missing_biometric_verified",
    ),
    USER_SEARCH_MISSING_CREATED_AT_GREATER_THAN(
        type = "user_search_missing_created_at_greater_than",
    ),
    USER_SEARCH_MISSING_CREATED_AT_LESS_THAN(
        type = "user_search_missing_created_at_less_than",
    ),
    USER_SEARCH_MISSING_CRYPTO_WALLET_ADDRESS(
        type = "user_search_missing_crypto_wallet_address",
    ),
    USER_SEARCH_MISSING_CRYPTO_WALLET_ID(
        type = "user_search_missing_crypto_wallet_id",
    ),
    USER_SEARCH_MISSING_CRYPTO_WALLET_VERIFIED(
        type = "user_search_missing_crypto_wallet_verified",
    ),
    USER_SEARCH_MISSING_EMAIL_ADDRESS_FUZZY(
        type = "user_search_missing_email_address_fuzzy",
    ),
    USER_SEARCH_MISSING_EMAIL_ADDRESSES(
        type = "user_search_missing_email_addresses",
    ),
    USER_SEARCH_MISSING_EMAIL_DOMAIN(
        type = "user_search_missing_email_domain",
    ),
    USER_SEARCH_MISSING_EMAIL_IDS(
        type = "user_search_missing_email_ids",
    ),
    USER_SEARCH_MISSING_EMAIL_VERIFIED(
        type = "user_search_missing_email_verified",
    ),
    USER_SEARCH_MISSING_FILTER_NAME(
        type = "user_search_missing_filter_name",
    ),
    USER_SEARCH_MISSING_FILTER_VALUE(
        type = "user_search_missing_filter_value",
    ),
    USER_SEARCH_MISSING_FULL_NAME_FUZZY(
        type = "user_search_missing_full_name_fuzzy",
    ),
    USER_SEARCH_MISSING_GREATER_THAN(
        type = "user_search_missing_greater_than",
    ),
    USER_SEARCH_MISSING_LESS_THAN(
        type = "user_search_missing_less_than",
    ),
    USER_SEARCH_MISSING_OAUTH_PROVIDERS(
        type = "user_search_missing_oauth_providers",
    ),
    USER_SEARCH_MISSING_PASSWORD(
        type = "user_search_missing_password",
    ),
    USER_SEARCH_MISSING_PHONE_IDS(
        type = "user_search_missing_phone_ids",
    ),
    USER_SEARCH_MISSING_PHONE_NUMBER_FUZZY(
        type = "user_search_missing_phone_number_fuzzy",
    ),
    USER_SEARCH_MISSING_PHONE_NUMBERS(
        type = "user_search_missing_phone_numbers",
    ),
    USER_SEARCH_MISSING_PHONE_VERIFIED(
        type = "user_search_missing_phone_verified",
    ),
    USER_SEARCH_MISSING_STATUS(
        type = "user_search_missing_status",
    ),
    USER_SEARCH_MISSING_TOTP_IDS(
        type = "user_search_missing_totp_ids",
    ),
    USER_SEARCH_MISSING_TOTP_VERIFIED(
        type = "user_search_missing_totp_verified",
    ),
    USER_SEARCH_MISSING_USER_IDS(
        type = "user_search_missing_user_ids",
    ),
    USER_SEARCH_MISSING_WEBAUTHN_REGISTRATION_IDS(
        type = "user_search_missing_webauthn_registration_ids",
    ),
    USER_SEARCH_MISSING_WEBAUTHN_VERIFIED(
        type = "user_search_missing_webauthn_verified",
    ),
    USER_SEARCH_PHONE_NUMBER_FUZZY_TOO_SHORT(
        type = "user_search_phone_number_fuzzy_too_short",
    ),
    USER_UNAUTHENTICATED(
        type = "user_unauthenticated",
    ),
    WEAK_FACTOR_COMBINATION(
        type = "weak_factor_combination",
    ),
    WEAK_PASSWORD(
        type = "weak_password",
    ),
    WEBAUTHN_REGISTRATION_NOT_FOUND(
        type = "webauthn_registration_not_found",
    ),
    WRONG_STACK_LIVE(
        type = "wrong_stack_live",
    ),
    WRONG_STACK_TEST(
        type = "wrong_stack_test",
    ),
    XML_VALIDATION_SAML_UNKNOWN_ERROR(
        type = "xml_validation_saml_unknown_error",
    ),
    ;

    internal companion object {
        fun fromString(typeString: String?): StytchAPIErrorType =
            try {
                valueOf(typeString?.uppercase(Locale.ENGLISH)!!)
            } catch (_: Exception) {
                UNKNOWN_ERROR
            }
    }
}
