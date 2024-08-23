package com.stytch.sdk.common.utils

import android.net.Uri

internal fun buildUri(
    url: String,
    parameters: Map<String, Any?>,
): Uri =
    Uri
        .parse(url)
        .buildUpon()
        .apply {
            parameters.forEach {
                if (it.value != null) {
                    when (it.value) {
                        is String -> appendQueryParameter(it.key, it.value.toString())
                        is List<*> -> appendQueryParameter(it.key, (it.value as List<*>).joinToString(" "))
                        is Map<*, *> -> {
                            // map type is only used for provider_params
                            (it.value as Map<*, *>).entries.forEach { entry ->
                                appendQueryParameter("provider_${entry.key}", entry.value.toString())
                            }
                        }
                    }
                }
            }
        }.build()
