package com.stytch.sdk.common.pkcePairManager

import com.stytch.sdk.common.PKCECodePair

internal interface PKCEPairManager {
    fun generateAndReturnPKCECodePair(): PKCECodePair

    fun getPKCECodePair(): PKCECodePair?

    fun clearPKCECodePair(): Unit
}
