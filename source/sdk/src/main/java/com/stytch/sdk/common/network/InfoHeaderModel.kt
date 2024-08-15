package com.stytch.sdk.common.network

import com.stytch.sdk.BuildConfig
import com.stytch.sdk.common.AUTH_HEADER_SDK_NAME
import com.stytch.sdk.common.DeviceInfo

internal data class InfoHeaderModel(
    val sdk: Item,
    val app: Item,
    val os: Item,
    val device: Item,
) {
    val json: String
        get() {
            return """
                {
                  "sdk": ${sdk.json},
                  "app": ${app.json},
                  "os": ${os.json},
                  "device": ${device.json}
                }
                """.trimIndent()
        }

    internal data class Item(
        val identifier: String,
        val version: String,
        val identifierName: String = "identifier",
        val versionName: String = "version",
    ) {
        val json: String
            get() {
                return "{ \"$identifierName\": \"$identifier\", \"$versionName\": \"$version\" }"
            }
    }

    companion object {
        fun fromDeviceInfo(deviceInfo: DeviceInfo): InfoHeaderModel =
            InfoHeaderModel(
                sdk =
                    Item(
                        AUTH_HEADER_SDK_NAME,
                        BuildConfig.STYTCH_SDK_VERSION,
                    ),
                app =
                    Item(
                        deviceInfo.applicationPackageName ?: "",
                        deviceInfo.applicationVersion ?: "",
                    ),
                os =
                    Item(
                        deviceInfo.osName ?: "",
                        deviceInfo.osVersion ?: "",
                    ),
                device =
                    Item(
                        deviceInfo.deviceName ?: "",
                        deviceInfo.screenSize ?: "",
                        "model",
                        "screen_size",
                    ),
            )
    }
}
