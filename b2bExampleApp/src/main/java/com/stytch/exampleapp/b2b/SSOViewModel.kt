package com.stytch.exampleapp.b2b

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.sso.SSO
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.sso.SSOError
import com.stytch.sdk.consumer.StytchClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SSOViewModel : ViewModel() {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    var ssoConnectionId by mutableStateOf(TextFieldValue(""))

    fun startSSO(context: Activity) {
        val params = SSO.StartParams(
            context = context,
            ssoAuthRequestIdentifier = SSO_REQUEST_ID,
            connectionId = ssoConnectionId.text,
        )
        StytchB2BClient.sso.start(params)
    }

    fun authenticateSSO(resultCode: Int, intent: Intent?) {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value = when (resultCode) {
                Activity.RESULT_OK -> {
                    intent?.data?.let {
                        val result = StytchClient.handle(it, 60U)
                        when (result) {
                            is DeeplinkHandledStatus.NotHandled -> result.reason.message
                            is DeeplinkHandledStatus.Handled -> result.response.result.toFriendlyDisplay()
                            // This only happens for password reset deeplinks
                            is DeeplinkHandledStatus.ManualHandlingRequired -> ""
                        }
                    } ?: "No URI found"
                }
                else -> {
                    intent?.extras?.getSerializable(SSOError.SSO_EXCEPTION)?.let {
                        when (it as SSOError) {
                            is SSOError.UserCanceled -> "User Canceled"
                            is SSOError.NoBrowserFound,
                            is SSOError.NoURIFound -> it.message
                        }
                    } ?: "Unknown Error"
                }
            }
        }
    }

    private fun CoroutineScope.launchAndToggleLoadingState(block: suspend () -> Unit): DisposableHandle {
        return launch {
            _loadingState.value = true
            block()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }
}
