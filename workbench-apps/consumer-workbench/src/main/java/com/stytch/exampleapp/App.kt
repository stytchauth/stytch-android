package com.stytch.exampleapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.stytch.sdk.consumer.StytchClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        StytchClient.configure(this) {
            println("Stytch has been initialized and configured and is ready for use")
            println("Last Auth Method Used: ${StytchClient.lastAuthMethodUsed}")
        }
        CoroutineScope(Dispatchers.Main).launch {
            StytchClient.sessions.onChange.collect {
                println("Collected session data: $it")
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            StytchClient.user.onChange.collect {
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
