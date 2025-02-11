package com.stytch.sdk.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

internal object NetworkChangeListener {
    private var callback: () -> Unit = {}
    var networkIsAvailable: Boolean = false
    private val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (!networkIsAvailable) {
                    callback()
                }
                networkIsAvailable = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                networkIsAvailable = false
            }
        }

    fun configure(
        context: Context,
        callback: () -> Unit,
    ) {
        this.callback = callback
        val networkRequest =
            NetworkRequest
                .Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }
}
