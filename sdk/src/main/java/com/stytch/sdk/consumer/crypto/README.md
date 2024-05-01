# Package com.stytch.sdk.consumer.crypto
The [CryptoWallet](CryptoWallet.kt) interface provides methods for authenticating a user with a crypto wallet.

Currently, Ethereum and Solana wallets are supported.

Call the `StytchClient.crypto.authenticateStart()` method to to load the challenge data. Pass this challenge to your user's wallet for signing.

Call the `StytchClient.crypto.authenticate()` method after the user signs the challenge to validate the signature. If this method succeeds and the user is not already logged in, the user will be logged in and granted an active session.

If the user is already logged in, the crypto wallet will be added to the `user.cryptoWallets` array and associated with user's existing session as an `authenticationFactor`.
