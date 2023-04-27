package com.stytch.sdk.b2b

import org.junit.Test

internal class B2BTokenTypeTest {
    @Test
    fun `B2BTokenType fromString returns expected values`() {
        assert(B2BTokenType.fromString("multi_tenant_magic_links") == B2BTokenType.MULTI_TENANT_MAGIC_LINKS)
        assert(B2BTokenType.fromString("discovery") == B2BTokenType.DISCOVERY)
        assert(B2BTokenType.fromString("multi_tenant_passwords") == B2BTokenType.MULTI_TENANT_PASSWORDS)
        assert(B2BTokenType.fromString("sso") == B2BTokenType.SSO)
        assert(B2BTokenType.fromString("something_unexpected") == B2BTokenType.UNKNOWN)
    }
}
