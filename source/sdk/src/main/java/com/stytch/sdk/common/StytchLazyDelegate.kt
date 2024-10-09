package com.stytch.sdk.common

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class StytchLazyDelegate<T>(
    private val assertInitialized: () -> Unit,
    private val initializer: () -> T,
) : ReadOnlyProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): T {
        assertInitialized()
        if (value == null) {
            value = initializer()
        }
        return value!!
    }
}
