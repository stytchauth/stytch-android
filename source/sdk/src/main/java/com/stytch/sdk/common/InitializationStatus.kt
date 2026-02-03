package com.stytch.sdk.common

public sealed class InitializationStatus {
    public data object Loading : InitializationStatus()

    public data object Success : InitializationStatus()

    public data class Failure(
        val errors: List<Throwable>,
    ) : InitializationStatus()
}
