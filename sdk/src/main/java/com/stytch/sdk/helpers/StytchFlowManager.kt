package com.stytch.sdk.helpers

import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchError
import com.stytch.sdk.StytchEvent
import com.stytch.sdk.StytchUI
import com.stytch.sdk.api.Api
import com.stytch.sdk.api.StytchResult
import com.stytch.sdk.exceptions.EmailNotFoundException
import com.stytch.sdk.exceptions.ExceptionRecognizer
import com.stytch.sdk.exceptions.UnknownException
import com.stytch.sdk.exceptions.WrongMagicLinkException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

class StytchFlowManager {

    private var email: String = ""
    private var emailId: String = ""
    private var userId: String = ""

    private var newUserCreated: Boolean = false

    fun login(email: String) {
        newUserCreated = false
        this.email = email
        GlobalScope.launch(Dispatchers.IO) {
            login()
        }
    }

    fun resendEmailVerification() {
        GlobalScope.launch(Dispatchers.IO) {
            verifyEmail(emailId, userId)
        }
    }

    fun verifyToken(token: String?) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (token == null) throw WrongMagicLinkException()

                val response = Api.instance.verifyToken(token)
                ExceptionRecognizer.recognize(response)
                if (response.body() == null) throw UnknownException()

                val result = StytchResult(
                    response.body()!!.user_id,
                    response.body()!!.request_id
                )
                withContext(Dispatchers.Main) {
                    Stytch.instance.listener?.onSuccess(result)
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
                handleError(ex)
            }

        }
    }

    private suspend fun login() {
        try {
            val response = Api.instance.sendMagicLink(email)
            ExceptionRecognizer.recognize(response)
            if (response.body() == null) throw UnknownException()
            withContext(Dispatchers.Main) {
                if(newUserCreated){
                    StytchUI.instance.uiListener?.onEvent(StytchEvent.userCreatedEvent(response.body()!!.user_id))
                } else {
                    StytchUI.instance.uiListener?.onEvent(StytchEvent.userFoundEvent(response.body()!!.user_id))
                }
                Stytch.instance.listener?.onMagicLinkSent(email)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            handleError(ex)
        }

    }

    private suspend fun createUser(email: String) {
        newUserCreated = true
        try {
            val response = Api.instance.createUser(email)
            ExceptionRecognizer.recognize(response)
            if (response.body() == null) throw UnknownException()
            emailId = response.body()!!.email_id
            if (Stytch.instance.config.verifyEmail) {
                verifyEmail(response.body()!!.email_id, response.body()!!.user_id)
            } else {
                login()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            handleError(ex)
        }

    }

    private suspend fun verifyEmail(emailId: String, userId: String) {
        try {
            val response = Api.instance.sendEmailVerification(emailId, userId)
            ExceptionRecognizer.recognize(response)
            if (response.body() == null) throw UnknownException()
//            withContext(Dispatchers.Main) {
//                Stytch.instance.listener?.onVerificationEmailSent(email)
//            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            handleError(ex)
        }
    }

    private suspend fun handleError(ex: Exception) {
        when (ex) {
            is UnknownHostException -> {
                withContext(Dispatchers.Main) {
                    Stytch.instance.listener?.onFailure(StytchError.Connection)
                }
            }
            is EmailNotFoundException -> {
                createUser(email)
            }
            is WrongMagicLinkException -> {
                withContext(Dispatchers.Main) {
                    Stytch.instance.listener?.onFailure(StytchError.InvalidMagicToken)
                }
            }
            else -> {
                withContext(Dispatchers.Main) {
                    Stytch.instance.listener?.onFailure(StytchError.Unknown)
                }
            }
        }
    }

    companion object {

        private const val TAG = "StytchFlowManager"

    }
}