package com.stytch.stytchexampleapp

import android.app.Application
import com.stytch.sdk.consumer.StytchClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        StytchClient.configure(applicationContext)
    }
}
