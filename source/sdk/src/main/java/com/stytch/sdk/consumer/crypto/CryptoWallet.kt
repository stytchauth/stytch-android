package com.stytch.sdk.consumer.crypto

import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.CryptoWalletAuthenticateStartResponse
import com.stytch.sdk.consumer.network.models.CryptoWalletType
import java.util.concurrent.CompletableFuture

/**
 * The CryptoWallet interface provides methods for authenticating a user using one of the supported Crypto Wallets
 * (Ethereum or Solana)
 */
public interface CryptoWallet {
    /**
     * A data class wrapping the parameters needed to begin a crypto wallet authentication flow
     * @property cryptoWalletAddress The address to authenticate.
     * @property cryptoWalletType The type of wallet to authenticate. Currently `ethereum` and `solana` are supported.
     */
    public data class AuthenticateStartParameters(
        val cryptoWalletAddress: String,
        val cryptoWalletType: CryptoWalletType,
    )

    /**
     * A data class wrapping the parameters needed to complete a crypto wallet authentication flow
     * @property cryptoWalletAddress The address to authenticate.
     * @property cryptoWalletType The type of wallet to authenticate. Currently `ethereum` and `solana` are supported.
     * @property signature The signature from the message.
     * @property sessionDurationMinutes the length of time in minutes that a session should be minted for
     */
    public data class AuthenticateParameters
        @JvmOverloads
        constructor(
            val cryptoWalletAddress: String,
            val cryptoWalletType: CryptoWalletType,
            val signature: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
        )

    /**
     * Begin a crypto wallet authentication flow
     * @param parameters the parameters required to begin a crypto wallet authentication flow
     * @return [CryptoWalletAuthenticateStartResponse]
     */
    public suspend fun authenticateStart(parameters: AuthenticateStartParameters): CryptoWalletAuthenticateStartResponse

    /**
     * Begin a crypto wallet authentication flow
     * @param parameters the parameters required to begin a crypto wallet authentication flow
     * @param callback a callback that receives a [CryptoWalletAuthenticateStartResponse]
     */
    public fun authenticateStart(
        parameters: AuthenticateStartParameters,
        callback: (CryptoWalletAuthenticateStartResponse) -> Unit,
    )

    /**
     * Begin a crypto wallet authentication flow
     * @param parameters the parameters required to begin a crypto wallet authentication flow
     * @return [CryptoWalletAuthenticateStartResponse]
     */
    public fun authenticateStartCompletable(
        parameters: AuthenticateStartParameters,
    ): CompletableFuture<CryptoWalletAuthenticateStartResponse>

    /**
     * Complete a crypto wallet authentication flow
     * @param parameters the parameters required to complete a crypto wallet authentication flow
     * @return [AuthResponse]
     */
    public suspend fun authenticate(parameters: AuthenticateParameters): AuthResponse

    /**
     * Complete a crypto wallet authentication flow
     * @param parameters the parameters required to complete a crypto wallet authentication flow
     * @param callback a callback that receives an [AuthResponse]
     */
    public fun authenticate(
        parameters: AuthenticateParameters,
        callback: (AuthResponse) -> Unit,
    )

    /**
     * Complete a crypto wallet authentication flow
     * @param parameters the parameters required to complete a crypto wallet authentication flow
     * @return [AuthResponse]
     */
    public fun authenticateCompletable(parameters: AuthenticateParameters): CompletableFuture<AuthResponse>
}
