package com.stytch.uiworkbench

import android.app.Application
import com.stytch.sdk.consumer.StytchClient

class UiWorkbenchApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        StytchClient.configure(this, BuildConfig.STYTCH_PUBLIC_TOKEN)
    }
}
