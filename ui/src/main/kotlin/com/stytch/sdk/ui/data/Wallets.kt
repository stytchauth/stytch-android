package com.stytch.sdk.ui.data

public enum class Wallets(public val displayName: String) {
    VESSEL("Vessel"),
    PHANTOM("Phantom"),
    METAMASK("Metamask"),
    COINBASE("Coinbase"),
    BINANCE("Binance"),
    GENERIC_ETHEREUM_WALLET("Other Ethereum Wallet"),
    GENERIC_SOLANA_WALLET("Other Solana Wallet")
}