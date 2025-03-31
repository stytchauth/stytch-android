package com.stytch.sdk.common

import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import java.util.Date

public sealed interface StytchObjectInfo<out T> {
    public data object Unavailable : StytchObjectInfo<Nothing>

    @JacocoExcludeGenerated
    public data class Available<out T>(
        val lastValidatedAt: Date,
        val value: T,
    ) : StytchObjectInfo<T>
}

internal inline fun <reified T> stytchObjectMapper(
    value: T?,
    lastValidatedAt: Date,
): StytchObjectInfo<T> =
    value?.let {
        StytchObjectInfo.Available(
            lastValidatedAt = lastValidatedAt,
            value = value,
        )
    } ?: StytchObjectInfo.Unavailable
