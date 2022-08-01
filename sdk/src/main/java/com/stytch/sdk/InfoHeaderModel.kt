package com.stytch.sdk

import com.squareup.moshi.Moshi
import org.json.JSONObject
import java.util.logging.Logger

internal data class InfoHeaderModel(
    val sdk: Item,
    val app: Item,
    val os: Item,
    val device: Item
)
{
    val json: String get(){
       return """
           {
             "sdk": ${sdk.json},
             "app": ${app.json},
             "os": ${os.json},
             "device": { "model": "Samsung", "screen_size": "(768,1024)" }
           }
 """.trimIndent().also {
           StytchLog.d(it)
       }
    }

    internal data class Item(
        val identifier: String,
        val version: String,
        val identifierName: String = "identifier",
        val versionName: String = "version",
    ){
        val json: String get() {
            return "{ \"$identifierName\": \"$identifier\", \"$versionName\": \"$version\" }"
        }
    }
}