package com.stytch.sdk

import android.content.Intent
import android.util.Base64
import android.util.Patterns
import java.io.Serializable

internal fun String.isValidEmailAddress(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

internal fun generateAuthorizationHeader(projectId: String, secret: String): String {
    return "Basic " + Base64.encodeToString(
        "$projectId:$secret".toByteArray(),
        Base64.NO_WRAP,
    )
}

internal fun assertInitialized() {
    if (!Stytch.isInitialized) {
        error("Stytch Error: Stytch not initialized. You must call 'Stytch.configure(...)' before using any functionality of the Stytch SDK.")
    }
}

internal inline fun <reified T : Serializable> intentWithExtra(extra: T): Intent {
    return Intent().apply {
        putExtra(T::class.qualifiedName, extra)
    }
}

public sealed class StytchUIResult {
    public data class Success(val user: StytchResponseTypes.AuthenticateUserResponse) : StytchUIResult()
    public object CancelledByUser : StytchUIResult()
}

internal fun Intent?.toStytchUIResultInternal(internalCall: Boolean = true): StytchUIResult {
    return when (this) {
        null -> StytchUIResult.CancelledByUser
        else -> {
            val user = getSerializableExtra<StytchResponseTypes.AuthenticateUserResponse>()
                ?: error(
                    if (internalCall) "Stytch Error: Internal Error. Could not deserialize user."
                    else "Stytch Error: Called Intent?.toStytchUIResult on an invalid Intent. Are you checking for the correct request code in onActivityResult()?"
                )
            StytchUIResult.Success(user)
        }
    }
}

public fun Intent?.toStytchUIResult(): StytchUIResult = toStytchUIResultInternal(false)

internal inline fun <reified T : Serializable> Intent.withSerializableExtra(extra: T): Intent = apply {
    putExtra(T::class.qualifiedName, extra)
}

internal inline fun <reified T : Serializable> Intent?.getSerializableExtra(): T? {
    return this?.extras?.getSerializable(T::class.qualifiedName) as? T
}

public object StytchErrorTypes {
    public const val EMAIL_NOT_FOUND: String = "email_not_found"
    public const val BILLING_NOT_VERIFIED_FOR_EMAIL: String = "billing_not_verified_for_email"
}
