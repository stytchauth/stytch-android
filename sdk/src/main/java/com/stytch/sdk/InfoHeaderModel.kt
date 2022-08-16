package com.stytch.sdk

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
}