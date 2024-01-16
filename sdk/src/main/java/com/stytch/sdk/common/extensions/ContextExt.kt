package com.stytch.sdk.common.extensions // ktlint-disable filename

import android.content.Context
import android.os.Build
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import java.io.File

internal fun Context.getDeviceInfo(): DeviceInfo {
    val deviceInfo = DeviceInfo()
    deviceInfo.applicationPackageName = applicationContext.packageName
    deviceInfo.osVersion = Build.VERSION.SDK_INT.toString()
    deviceInfo.deviceName = Build.MODEL
    deviceInfo.osName = "Android"

    try {
        // throw exceptions if packageName not found
        deviceInfo.applicationVersion = applicationContext
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

internal fun Context.clearPreferences() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        deleteSharedPreferences(EncryptionManager.PREF_FILE_NAME)
    } else {
        getSharedPreferences(EncryptionManager.PREF_FILE_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        val dir = File(applicationInfo.dataDir, "shared_prefs")
        File(dir, "${EncryptionManager.PREF_FILE_NAME}.xml").delete()
    }
}
