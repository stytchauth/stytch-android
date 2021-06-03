package com.stytch.sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Patterns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.Serializable

internal fun String.isValidEmailAddress(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
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

public fun Intent?.toStytchUIResult(): StytchUIResult {
    disposeAllScreens()
    return when (this) {
        null -> StytchUIResult.CancelledByUser
        else -> {
            val user = getSerializableExtra<StytchResponseTypes.AuthenticateUserResponse>()
                ?: error("Stytch Error: Called Intent?.toStytchUIResult on an invalid Intent. Are you checking for the correct request code in onActivityResult()?")
            StytchUIResult.Success(user)
        }
    }
}

internal fun <T : Activity> stytchIntent(context: Context, activity: Class<T>): Intent {
    return Intent(context, activity).apply {
        putExtra(Constants.ACTIVITY_ID_EXTRA_NAME, StytchActivity.getNextActivityId())
    }
}

internal fun disposeAllScreens() {
    StytchActivity.navigator = null
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

internal object Constants {
    const val ACTIVITY_ID_EXTRA_NAME = "activity_id"
}

@Suppress("FunctionName")
internal fun StytchScreenViewCoroutineScope(): CoroutineScope {
    return CoroutineScope(SupervisorJob()) // TODO
}

internal inline fun <T> CoroutineScope.listen(stateFlow: StateFlow<T>, crossinline collectBlock: suspend (T) -> Unit) {
    launch(Dispatchers.Main) {
        stateFlow.collect(collectBlock)
    }
}
