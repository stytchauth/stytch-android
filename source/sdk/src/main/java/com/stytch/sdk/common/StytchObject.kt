package com.stytch.sdk.common

import java.util.Date

public sealed interface StytchObject<out T> {
    public data object Unavailable : StytchObject<Nothing>

    public data class Available<out T>(
        val lastValidatedAt: Date,
        val value: T,
    ) : StytchObject<T>
}

internal inline fun <reified T> stytchObjectMapper(
    value: T?,
    lastValidatedAt: Date,
): StytchObject<T> =
    value?.let {
        StytchObject.Available(
            lastValidatedAt = lastValidatedAt,
            value = value,
        )
    } ?: StytchObject.Unavailable
