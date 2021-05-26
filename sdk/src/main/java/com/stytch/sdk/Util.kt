package com.stytch.sdk

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Patterns
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
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

public class StytchUIResult

public fun ActivityResultCaller.registerStytchEmailMagicLinkActivity(onResult: (StytchUIResult) -> Unit) : ActivityResultLauncher<StytchUI.EmailMagicLink.Configuration> {
    return registerForActivityResult(stytchEmailMagicLinkActivityContract, onResult)
}

public fun ActivityResultCaller.registerStytchSMSPasscodeActivity(onResult: (StytchUIResult) -> Unit) : ActivityResultLauncher<StytchUI.SMSPasscode.Configuration> {
    return registerForActivityResult(stytchSMSPasscodeActivityContract, onResult)
}

internal val stytchEmailMagicLinkActivityContract by lazy {
    object : ActivityResultContract<StytchUI.EmailMagicLink.Configuration, StytchUIResult>() {
        override fun createIntent(context: Context, input: StytchUI.EmailMagicLink.Configuration): Intent {
            return Intent(context, StytchEmailMagicLinkActivity::class.java).withSerializableExtra(input)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): StytchUIResult {
            return intent.toStytchUIResult()
        }
    }
}

internal val stytchSMSPasscodeActivityContract by lazy {
    object : ActivityResultContract<StytchUI.SMSPasscode.Configuration, StytchUIResult>() {
        override fun createIntent(context: Context, input: StytchUI.SMSPasscode.Configuration): Intent {
            return Intent(context, StytchEmailMagicLinkActivity::class.java).withSerializableExtra(input)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): StytchUIResult {
            return intent.toStytchUIResult()
        }
    }
}

internal fun Intent?.toStytchUIResult(): StytchUIResult {
    return StytchUIResult()
}

internal inline fun <reified T : Serializable> Intent.withSerializableExtra(extra: T): Intent = apply {
    putExtra(T::class.qualifiedName, extra)
}

internal inline fun <reified T : Serializable> Intent?.getSerializableExtra(): T? {
    return this?.extras?.getSerializable(T::class.qualifiedName) as? T
}
