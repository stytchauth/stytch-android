package com.stytch.sdk.ui

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stytch.sdk.R
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchError
import com.stytch.sdk.StytchUI
import com.stytch.sdk.api.StytchResult
import com.stytch.sdk.helpers.ErrorManager
import com.stytch.sdk.helpers.Event
import com.stytch.sdk.helpers.LoggerLocal

internal class LoginViewModel : ViewModel() {

    val stateLiveData = MutableLiveData<State>().apply { value = State.Login }
    val loadingLiveData = MutableLiveData<Boolean>().apply { value = false }
    val errorManager = ErrorManager()
    val closeLiveData = MutableLiveData<Event<Boolean>>()

    val stytchListener = object : Stytch.StytchListener {
        override fun onSuccess(result: StytchResult) {
            loadingLiveData.value = false
            StytchUI.instance.uiListener?.onSuccess(result)
            closeLiveData.value = Event(true)
        }

        override fun onFailure(error: StytchError) {
            loadingLiveData.value = false
            when (error) {
                StytchError.InvalidMagicToken,
                StytchError.Connection -> {
                    errorManager.showError(error.messageId)
                }
                StytchError.Unknown,
                StytchError.InvalidEmail -> {
                    errorManager.showError(error.messageId)
                    stateLiveData.value = State.Login
                }
                StytchError.InvalidConfiguration -> {
                    loadingLiveData.value = true
                    StytchUI.instance.uiListener?.onFailure()
                    closeLiveData.value = Event(true)
                }
            }
        }

        override fun onMagicLinkSent(email: String) {
            LoggerLocal.d(TAG, "sendMagicLink success")
            loadingLiveData.value = false
        }
    }.apply {
        Stytch.instance.listener = this
    }

    fun signInClicked(email: String?) {
        LoggerLocal.d(TAG, "checkEmail: $email")
        if (email.isNullOrBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorManager.showError(R.string.stytch_error_invalid_input)
            return
        }
        loadingLiveData.value = true
        stateLiveData.value = State.WaitingVerification
        Stytch.instance.login(email)
    }

    fun resendClicked() {
        stateLiveData.value = State.Login
    }

    fun verifyToken(uri: Uri) {
        loadingLiveData.value = true
        Stytch.instance.handleDeepLink(uri)
    }

    enum class State {
        Login,
        WaitingVerification
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}
