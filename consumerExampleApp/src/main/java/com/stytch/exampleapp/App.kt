package com.stytch.exampleapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.stytch.sdk.consumer.StytchClient
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Timber.plant(Timber.DebugTree())
        StytchClient.configure(
            context = this,
            publicToken = BuildConfig.STYTCH_PUBLIC_TOKEN,
        ) {
            println("Stytch has been initialized and configured and is ready for use")
        }
    }
}
