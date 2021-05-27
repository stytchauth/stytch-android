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

public inline fun <reified T : Serializable> intentWithExtra(extra: T): Intent {
    return Intent().apply {
        putExtra(T::class.qualifiedName, extra)
    }
}

public sealed class StytchUIResult {
    public data class Success(val user: StytchResponseTypes.AuthenticateUserResponse) : StytchUIResult()
    public object CancelledByUser : StytchUIResult()
}

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
    return when (this) {
        null -> StytchUIResult.CancelledByUser
        else -> {
            val user = getSerializableExtra<StytchResponseTypes.AuthenticateUserResponse>()
                ?: error("Stytch Error: Internal Error. Could not deserialize user.")
            StytchUIResult.Success(user)
        }
    }
}

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
