package com.stytch.sdk.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stytch.sdk.R
import com.stytch.sdk.api.Api
import com.stytch.sdk.exceptions.EmailNotFoundException
import com.stytch.sdk.helpers.ErrorManager
import com.stytch.sdk.helpers.Event
import com.stytch.sdk.helpers.LoggerLocal

class LoginViewModel : ViewModel() {


    val stateLiveData = MutableLiveData<State>().apply { value = State.Login }
    val loadingLiveData = MutableLiveData<Boolean>().apply { value = false }
    val loggedInSuccessfullyLiveData = MutableLiveData<Event<Boolean>>()
    val errorManager = ErrorManager()

    fun signInClicked(email: String?) {

//        TODO: check email is valid & show error if needed
        val email = "demid@stytch.com"
        if (email == null) {
            return
        }
//        TODO: do smth with email
        Api.instance.sendMagicLink(email, onSuccess = {
            loadingLiveData.value = false
            LoggerLocal.d(TAG, "sendMagicLink success")
        }, onError = {
            when (it) {
                is EmailNotFoundException -> {
                    createNewUser(email)
                }
                else -> {
                    errorManager.showError(it)
                }
            }
        })

        loadingLiveData.value = true
        stateLiveData.value = State.WaitingVerification
    }

    private fun createNewUser(email: String) {
        Api.instance.createUser(
            email,
            onSuccess = {
                LoggerLocal.d(TAG, "createNewUser success")
                signInClicked(email)
            },
            onError = {
                errorManager.showError(it)
            })
    }

    fun verifyToken(token: String?){
        if(token == null) {
            errorManager.showError(R.string.stytch_error_bad_token)
            return
        }
        Api.instance.verifyToken(
            token,
            onSuccess = {
                LoggerLocal.d(TAG, "verifyToken success")
            },
            onError = {
                errorManager.showError(it)
            })
    }

    fun resendClicked() {
        loadingLiveData.value = true
        stateLiveData.value = State.Login
    }


    enum class State {
        Login,
        WaitingVerification
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }

}