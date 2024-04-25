package com.stytch.sdk.consumer.crypto

import com.stytch.sdk.common.Constants
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.CryptoWalletAuthenticateStartResponse
import com.stytch.sdk.consumer.network.models.CryptoWalletType

public interface CryptoWallet {
    /**
     * TODO
     */
    public data class AuthenticateStartParameters(
        val cryptoWalletAddress: String,
        val cryptoWalletType: CryptoWalletType,
    )

    /**
     * TODO
     */
    public data class AuthenticateParameters(
        val cryptoWalletAddress: String,
        val cryptoWalletType: CryptoWalletType,
        val signature: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * TODO
     */
    public suspend fun authenticateStart(parameters: AuthenticateStartParameters): CryptoWalletAuthenticateStartResponse

    /**
     * TODO
     */
    public fun authenticateStart(
        parameters: AuthenticateStartParameters,
        callback: (CryptoWalletAuthenticateStartResponse) -> Unit,
    )

    /**
     * TODO
     */
    public suspend fun authenticate(parameters: AuthenticateParameters): AuthResponse

    /**
     * TODO
     */
    public fun authenticate(
        parameters: AuthenticateParameters,
        callback: (AuthResponse) -> Unit,
    )
}
