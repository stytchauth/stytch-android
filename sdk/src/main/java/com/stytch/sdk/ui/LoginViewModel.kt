package com.stytch.sdk.ui

import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stytch.sdk.BlankWarningException
import com.stytch.sdk.LoggerLocal
import com.stytch.sdk.R
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchError
import com.stytch.sdk.StytchResult
import com.stytch.sdk.StytchUI
import com.stytch.sdk.WarningException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                StytchError.Connection,
                -> {
                    errorManager.showError(error.messageId)
                }
                StytchError.Unknown,
                StytchError.InvalidEmail,
                -> {
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
        WaitingVerification,
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}

internal class ErrorManager {

    private val errorMessage = MutableLiveData<Event<String>>()
    private val errorMessageId = MutableLiveData<Event<Int>>()

    fun showError(message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            errorMessage.value = Event(message)
        }
    }

    fun showError(messageId: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            errorMessageId.value = Event(messageId)
        }
    }

    fun showError(error: Throwable) {
        LoggerLocal.e(TAG, "catch error: $error")
        error.printStackTrace()
        when (error) {
            is BlankWarningException -> {
                if (error.errorMessage != null) {
                    showError(error.errorMessage)
                } else {
                    showError(R.string.stytch_error_unknown)
                }

            }
            is WarningException -> {
                showError(error.errorMessageId)
            }
            else -> {
                showError(R.string.stytch_error_unknown)
            }
        }

    }

    fun showUnknownError() {
        GlobalScope.launch(Dispatchers.Main) {
            errorMessageId.value = Event(R.string.stytch_error_unknown)
        }
    }

    fun observeErrors(fragment: Fragment, viewLifecycleOwner: LifecycleOwner, tag: String) {
        val view = fragment.view
        GlobalScope.launch(Dispatchers.Main) {
            errorMessage.observe(viewLifecycleOwner, { event ->
                event.getEventNotHandled()?.let {
                    LoggerLocal.e(tag, it)
                    view?.let { view ->
                        AlertDialog.Builder(fragment.requireContext())
                            .setTitle(it)
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                    }
                }
            })
            errorMessageId.observe(viewLifecycleOwner, { event ->
                event.getEventNotHandled()?.let {
                    view?.let { view ->
                        AlertDialog.Builder(fragment.requireContext())
                            .setTitle(it)
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                    }
                }
            })
        }
    }

    companion object {
        private const val TAG = "ErrorManager"
    }
}

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
internal open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getEventNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content

    override fun toString(): String {
        return "Event(${content.toString()})"
    }
}
