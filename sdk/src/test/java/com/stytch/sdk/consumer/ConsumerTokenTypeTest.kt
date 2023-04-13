package com.stytch.sdk.consumer

import org.junit.Test

internal class ConsumerTokenTypeTest {
    @Test
    fun `B2BTokenType fromString returns expected values`() {
        assert(ConsumerTokenType.fromString("magic_links") == ConsumerTokenType.MAGIC_LINKS)
        assert(ConsumerTokenType.fromString("oauth") == ConsumerTokenType.OAUTH)
        assert(ConsumerTokenType.fromString("password_reset") == ConsumerTokenType.PASSWORD_RESET)
        assert(ConsumerTokenType.fromString("something_unexpected") == ConsumerTokenType.UNKNOWN)
    }
}
