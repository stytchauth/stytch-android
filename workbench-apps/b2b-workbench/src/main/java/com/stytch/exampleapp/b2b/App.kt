package com.stytch.exampleapp.b2b

import android.app.Application
import com.stytch.sdk.b2b.StytchB2BClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        StytchB2BClient.configure(this)
        StytchB2BClient.configure(applicationContext) {
            println("Last Auth Method Used: ${StytchB2BClient.lastAuthMethodUsed}")
        }
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
