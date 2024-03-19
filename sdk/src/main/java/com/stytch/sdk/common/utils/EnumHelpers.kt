package com.stytch.sdk.common.utils

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

internal interface IEnumValue {
    val jsonName: String
}

internal object GenericEnumFactory {
    inline fun <reified T> fromValueOrNull(rawString: String?): T? where T : Enum<T>, T : IEnumValue {
        return enumValues<T>().firstOrNull { it.jsonName == rawString }
    }
}

internal inline fun <reified T> createEnumJsonAdapter(): JsonAdapter<T> where T : Enum<T>, T : IEnumValue {
    return object : JsonAdapter<T>() {
        @FromJson
        override fun fromJson(reader: JsonReader): T? {
            return if (reader.peek() != JsonReader.Token.NULL) {
                val value = reader.nextString()
                enumValues<T>().firstOrNull { it.jsonName == value }
            } else {
                reader.nextNull()
            }
        }

        @ToJson
        override fun toJson(
            writer: JsonWriter,
            value: T?,
        ) {
            writer.value(value?.jsonName)
        }
    }
}
