package com.stytch.sdk.common

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

internal object AppLifecycleListener {
    private var callback: () -> Unit = {}

    private val listener =
        object : DefaultLifecycleObserver {
            var wasBackgrounded = false

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                if (wasBackgrounded) {
                    callback()
                }
                wasBackgrounded = false
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                wasBackgrounded = true
            }
        }

    fun configure(callback: () -> Unit) {
        this.callback = callback
        ProcessLifecycleOwner.get().lifecycle.addObserver(listener)
    }
}
