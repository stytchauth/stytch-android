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

public fun Intent?.toStytchUser(): StytchResponseTypes.AuthenticateUserResponse {
    try {
        return this!!.extras!!.getSerializable(StytchResponseTypes.AuthenticateUserResponse::class.qualifiedName) as StytchResponseTypes.AuthenticateUserResponse
    } catch (throwable: Throwable) {
        error("Stytch Error: called 'Intent?.toStytchUser()' on an invalid Intent. Did you make sure that 'resultCode == Activity.RESULT_OK'?")
    }
}

public fun Intent?.toStytchError(): StytchError {
    return when (this) {
        null -> StytchError.CancelledByUser
        else -> StytchError.Unknown
    }
}

public sealed class StytchError private constructor() {
    public object CancelledByUser : StytchError()
    public object Unknown : StytchError()
}

public inline fun <reified T : Serializable> intentWithExtra(extra: T): Intent {
    return Intent().apply {
        putExtra(T::class.qualifiedName, extra)
    }
}
