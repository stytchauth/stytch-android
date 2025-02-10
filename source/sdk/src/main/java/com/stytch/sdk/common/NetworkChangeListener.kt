package com.stytch.sdk.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

internal object NetworkChangeListener {
    private var callback: () -> Unit = {}
    private val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
            var networkWasLost = false

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (networkWasLost) {
                    callback()
                }
                networkWasLost = false
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                networkWasLost = true
            }
        }
    private val networkRequest =
        NetworkRequest
            .Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

    fun configure(
        context: Context,
        callback: () -> Unit,
    ) {
        this.callback = callback
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        callback()
    }
}
