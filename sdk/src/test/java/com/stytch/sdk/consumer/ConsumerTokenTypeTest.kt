package com.stytch.sdk.consumer

import org.junit.Test

internal class ConsumerTokenTypeTest {
    @Test
    fun `ConsumerTokenType fromString returns expected values`() {
        assert(ConsumerTokenType.fromString("magic_links") == ConsumerTokenType.MAGIC_LINKS)
        assert(ConsumerTokenType.fromString("oauth") == ConsumerTokenType.OAUTH)
        assert(ConsumerTokenType.fromString("reset_password") == ConsumerTokenType.RESET_PASSWORD)
        assert(ConsumerTokenType.fromString("something_unexpected") == ConsumerTokenType.UNKNOWN)
    }
}
