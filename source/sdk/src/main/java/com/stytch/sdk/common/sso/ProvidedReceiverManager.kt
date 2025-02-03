package com.stytch.sdk.common.sso

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.stytch.sdk.common.QUERY_TOKEN
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.NoBrowserFound
import com.stytch.sdk.common.errors.NoURIFound
import com.stytch.sdk.common.errors.UnknownOAuthOrSSOError
import com.stytch.sdk.common.errors.UserCanceled
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

internal object ProvidedReceiverManager {
    private var activity: ComponentActivity? = null
    private var continuation: Continuation<StytchResult<String>>? = null
    private var launcher: ActivityResultLauncher<Intent>? = null

    internal fun getReceiverConfiguration(
        continuation: Continuation<StytchResult<String>>,
    ): Pair<ComponentActivity?, ActivityResultLauncher<Intent>?> {
        this.continuation = continuation
        return Pair(activity, launcher)
    }

    internal fun configureReceiver(activity: ComponentActivity?) {
        this.activity = activity
        launcher =
            activity?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val response =
                    when (result.resultCode) {
                        RESULT_OK -> {
                            result.data?.data?.getQueryParameter(QUERY_TOKEN)?.let {
                                StytchResult.Success(it)
                            } ?: StytchResult.Error(NoURIFound)
                        }

                        else -> {
                            val error =
                                result.data?.extras?.getSerializable(SSOError.SSO_EXCEPTION)?.let {
                                    when (it as SSOError) {
                                        is SSOError.UserCanceled -> UserCanceled
                                        is SSOError.NoBrowserFound -> NoBrowserFound
                                        is SSOError.NoURIFound -> NoURIFound
                                    }
                                } ?: UnknownOAuthOrSSOError
                            StytchResult.Error(error)
                        }
                    }
                continuation?.resume(response)
                continuation = null
            }
    }
}
