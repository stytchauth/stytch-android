package com.stytch.sdk.helpers

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.stytch.sdk.R
import com.stytch.sdk.exceptions.BlankWarningException
import com.stytch.sdk.exceptions.WarningException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "ErrorManager"

class ErrorManager {

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
//        TODO: show error in appropriate View
        GlobalScope.launch(Dispatchers.Main) {
            val view = fragment.view
            errorMessage.observe(viewLifecycleOwner, Observer { event ->
                event.getEventNotHandled()?.let {
                    LoggerLocal.e(tag, it)
                    view?.let { view ->
//
                        Toast.makeText(view.context, it, Toast.LENGTH_LONG).show()
//                        Snackbar.make(view, it, Snackbar.LENGTH_LONG).config(view.context).show()
                    }
                }
            })
            errorMessageId.observe(viewLifecycleOwner, Observer { event ->
                event.getEventNotHandled()?.let {
                    view?.let { view ->
                        Toast.makeText(view.context, it, Toast.LENGTH_LONG).show()
//                        Snackbar.make(view, it, Snackbar.LENGTH_LONG).config(view.context).show()
                    }
                }
            })
        }
    }
}
