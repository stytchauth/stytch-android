package com.stytch.sdk.ui.b2b.data

internal enum class ProductComponent {
    EmailForm,
    EmailDiscoveryForm,
    OAuthButtons,
    SSOButtons,
    PasswordsEmailForm,
    PasswordEMLCombined,
    Divider,
    ;

    internal fun isInputComponent(): Boolean =
        this == EmailForm ||
            this == EmailDiscoveryForm ||
            this == PasswordsEmailForm ||
            this == PasswordEMLCombined
}
