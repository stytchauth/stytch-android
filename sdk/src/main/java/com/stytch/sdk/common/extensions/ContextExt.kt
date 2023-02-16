package com.stytch.sdk.common.extensions // ktlint-disable filename

import android.content.Context
import android.os.Build
import com.stytch.sdk.common.DeviceInfo

internal fun Context.getDeviceInfo(): DeviceInfo {
    val deviceInfo = DeviceInfo()
    deviceInfo.applicationPackageName = applicationContext.packageName
    deviceInfo.osVersion = Build.VERSION.SDK_INT.toString()
    deviceInfo.deviceName = Build.MODEL
    deviceInfo.osName = Build.VERSION.CODENAME

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
