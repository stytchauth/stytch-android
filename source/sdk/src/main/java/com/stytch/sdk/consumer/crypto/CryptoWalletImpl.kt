package com.stytch.sdk.consumer.crypto

import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.consumer.ConsumerAuthMethod
import com.stytch.sdk.consumer.CryptoWalletAuthenticateStartResponse
import com.stytch.sdk.consumer.CryptoWalletsAuthenticateResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

internal class CryptoWalletImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.Crypto,
) : CryptoWallet {
    override suspend fun authenticateStart(
        parameters: CryptoWallet.AuthenticateStartParameters,
    ): CryptoWalletAuthenticateStartResponse =
        withContext(dispatchers.io) {
            if (sessionStorage.persistedSessionIdentifiersExist) {
                api.authenticateStartSecondary(
                    cryptoWalletAddress = parameters.cryptoWalletAddress,
                    cryptoWalletType = parameters.cryptoWalletType,
                )
            } else {
                api.authenticateStartPrimary(
                    cryptoWalletAddress = parameters.cryptoWalletAddress,
                    cryptoWalletType = parameters.cryptoWalletType,
                )
            }
        }

    override fun authenticateStart(
        parameters: CryptoWallet.AuthenticateStartParameters,
        callback: (CryptoWalletAuthenticateStartResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(authenticateStart(parameters))
        }
    }

    override fun authenticateStartCompletable(
        parameters: CryptoWallet.AuthenticateStartParameters,
    ): CompletableFuture<CryptoWalletAuthenticateStartResponse> =
        externalScope
            .async {
                authenticateStart(parameters)
            }.asCompletableFuture()

    override suspend fun authenticate(
        parameters: CryptoWallet.AuthenticateParameters,
    ): CryptoWalletsAuthenticateResponse =
        withContext(dispatchers.io) {
            api
                .authenticate(
                    cryptoWalletAddress = parameters.cryptoWalletAddress,
                    cryptoWalletType = parameters.cryptoWalletType,
                    signature = parameters.signature,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).apply {
                    sessionStorage.lastAuthMethodUsed = ConsumerAuthMethod.CRYPTO
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }

    override fun authenticate(
        parameters: CryptoWallet.AuthenticateParameters,
        callback: (CryptoWalletsAuthenticateResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(authenticate(parameters))
        }
    }

    override fun authenticateCompletable(
        parameters: CryptoWallet.AuthenticateParameters,
    ): CompletableFuture<CryptoWalletsAuthenticateResponse> =
        externalScope
            .async {
                authenticate(parameters)
            }.asCompletableFuture()
}
