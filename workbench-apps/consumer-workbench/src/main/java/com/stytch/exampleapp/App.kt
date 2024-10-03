package com.stytch.exampleapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.stytch.sdk.consumer.StytchClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        CoroutineScope(Dispatchers.Main).launch {
            StytchClient.sessions.onChange().collect {
                println("Collected session data: $it")
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            StytchClient.user.onChange().collect {
                println("Collected user data: $it")
            }
        }
        StytchClient.sessions.onChange {
            println("Callback session data: $it")
        }
        StytchClient.user.onChange {
            println("Callback user data: $it")
        }
    }
}
