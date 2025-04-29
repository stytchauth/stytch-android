@file:Suppress("ktlint:standard:filename")

package com.stytch.sdk.common.extensions

import android.content.Context
import android.os.Build
import com.stytch.sdk.common.DeviceInfo
import java.io.File

internal fun Context.getDeviceInfo(): DeviceInfo {
    val deviceInfo = DeviceInfo()
    deviceInfo.applicationPackageName = applicationContext.packageName
    deviceInfo.osVersion = Build.VERSION.SDK_INT.toString()
    deviceInfo.deviceName = Build.MODEL
    deviceInfo.osName = "Android"

    try {
        // throw exceptions if packageName not found
        deviceInfo.applicationVersion =
            applicationContext
                .packageManager
                .getPackageInfo(deviceInfo.applicationPackageName!!, 0)
                .versionName
    } catch (ex: Exception) {
        deviceInfo.applicationVersion = ""
    }

    val width = resources.displayMetrics.widthPixels
    val height = resources.displayMetrics.heightPixels

    deviceInfo.screenSize = "($width,$height)"
    return deviceInfo
}

internal fun Context.clearPreferences(preferenceFileNames: List<String>) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        preferenceFileNames.forEach {
            deleteSharedPreferences(it)
        }
    } else {
        preferenceFileNames.forEach {
            getSharedPreferences(it, Context.MODE_PRIVATE).edit().clear().apply()
            val dir = File(applicationInfo.dataDir, "shared_prefs")
            File(dir, "$it.xml").delete()
        }
    }
}
