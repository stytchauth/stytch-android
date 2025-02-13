package com.stytch.exampleapp.b2b

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.stytch.sdk.b2b.StytchB2BClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Timber.plant(Timber.DebugTree())
        StytchB2BClient.configure(applicationContext)
        CoroutineScope(Dispatchers.Main).launch {
            StytchB2BClient.sessions.onChange.collect {
                println("Collected session data: $it")
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            StytchB2BClient.member.onChange.collect {
                println("Collected member data: $it")
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            StytchB2BClient.organization.onChange.collect {
                println("Collected organization data: $it")
            }
        }
        StytchB2BClient.sessions.onChange {
            println("Callback session data: $it")
        }
        StytchB2BClient.member.onChange {
            println("Callback member data: $it")
        }
        StytchB2BClient.organization.onChange {
            println("Callback organization data: $it")
        }
    }
}
