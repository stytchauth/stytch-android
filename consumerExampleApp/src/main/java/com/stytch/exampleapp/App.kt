package com.stytch.exampleapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Timber.plant(Timber.DebugTree())
    }
}
