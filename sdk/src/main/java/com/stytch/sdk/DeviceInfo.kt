package com.stytch.sdk

internal data class DeviceInfo(
    var applicationPackageName: String? = null,
    var applicationVersion: String? = null,
    var osName: String? = null,
    var osVersion: String? = null,
    var deviceName: String? = null,
    var screenSize: String? = null,
)
