# Package com.stytch.sdk.b2b.sso

Single-Sign On (SSO) refers to the ability for a user to use a single identity to authenticate and gain access to multiple apps and service. In the case of B2B, it generally refers for the ability to use a workplace identity managed by their company. Read our [blog post](https://stytch.com/blog/single-sign-on-sso/) for more information about SSO.

Stytch supports the following SSO protocols:
- SAML

To start a SAML authentication request, call the `StytchB2BClient.sso.start()` method.

To authenticate an SSO token, call the `StytchB2BClient.sso.authenticate()` method.