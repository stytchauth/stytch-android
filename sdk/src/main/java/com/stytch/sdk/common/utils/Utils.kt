package com.stytch.sdk.common.utils

import com.stytch.sdk.common.PKCECodePair

/**
 * An interface that defines utility functions useful for interacting with the StytchClient, but not directly associated
 * with an authentication product provided by Stytch
 */
public interface Utils {
    /**
     * Retrieve the most recently stored PKCE code pair (challenge and verifier), if they exist
     * @returns [PKCECodePair]
     */
    public suspend fun getPKCEPair(): PKCECodePair

    /**
     * Retrieve the most recently stored PKCE code pair (challenge and verifier), if they exist
     * @param callback a callback that receives a [PKCECodePair]
     */
    public fun getPKCEPair(callback: (PKCECodePair) -> Unit)
}
