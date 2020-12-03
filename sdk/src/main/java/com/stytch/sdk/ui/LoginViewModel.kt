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

class LoginViewModel : ViewModel() {


    val stateLiveData = MutableLiveData<State>().apply { value = State.Login }
    val loadingLiveData = MutableLiveData<Boolean>().apply { value = false }
    val errorManager = ErrorManager()

    val stytchListener = object : Stytch.StytchListener {
        override fun onSuccess(result: StytchResult) {
            StytchUI.instance.uiListener?.onSuccess(result)
        }

        override fun onFailure(error: StytchError) {
            loadingLiveData.value = false
            errorManager.showError(error.name)
            StytchUI.instance.uiListener?.onFailure()
        }

        override fun onMagicLinkSent(email: String) {
            LoggerLocal.d(TAG, "sendMagicLink success")
            loadingLiveData.value = false
        }
    }.apply {
        Stytch.instance.listener = this
    }

    fun signInClicked(email: String?) {
//        "email-test-12fb246b-e78d-4bb9-9e38-f6b7796b4a86"
//        user-test-ff7a8219-70b5-462d-9ec0-ef858fdbdf5f
        LoggerLocal.d(TAG,"checkEmail: $email")
        if(email.isNullOrBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errorManager.showError(R.string.stytch_error_invalid_input)
            return
        }
        loadingLiveData.value = true
        stateLiveData.value = State.WaitingVerification
        Stytch.instance.login(email)
    }


    fun resendClicked() {
//        loadingLiveData.value = true
        stateLiveData.value = State.Login
    }

    fun verifyToken(uri: Uri) {
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