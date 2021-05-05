package com.stytch.sdk

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

internal object Constants {
    const val HOST = "stytch.com"
    const val NOT_INITIALIZED_WARNING = "Stytch is not configured. call Stytch.instance.configure(...) first"
    const val LOGIN_PATH = "login_magic_link"
    const val INVITE_PATH = "invite_magic_link"
    const val SIGN_UP_PATH = "signup_magic_link"

    const val LOGIN_EXPIRATION = 60L
    const val INVITE_EXPIRATION = 7 * 24 * 60L
    const val SIGNUP_EXPIRATION = 7 * 24 * 60L
}

internal class CustomTypefaceSpan(family: String?, private val newType: Typeface) : TypefaceSpan(family) {
    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    companion object {
        private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
            val oldStyle: Int
            val old = paint.typeface
            oldStyle = old?.style ?: 0
            val fake = oldStyle and tf.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }
            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }
            paint.typeface = tf
        }
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

public val Number.px: Float
    get() = this.toFloat() / Resources.getSystem().displayMetrics.density

public val Number.dp: Float
    get() = this.toFloat() * Resources.getSystem().displayMetrics.density

internal fun Fragment.hideKeyboard() {
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
}

internal fun Int.invertedWhiteBlack(): Int {
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    return if ((red * 0.299 + green * 0.587 + blue * 0.114) > 140) Color.BLACK else Color.WHITE
}

internal fun String.deepLink(): String {
    return "${Stytch.instance.config?.deepLinkScheme}://${Stytch.instance.config?.deepLinkHost}/$this"
}

public class LoggerLocal {

    public companion object {
        public fun d(tag: String, msg: String, throwable: Throwable? = null) {
            if (BuildConfig.DEBUG) {
                Log.d(tag, msg, throwable)
            }
        }

        public fun e(tag: String, msg: String, throwable: Throwable? = null) {
            if (BuildConfig.DEBUG) {
                Log.e(tag, msg, throwable)
            }
        }

        public fun w(tag: String, msg: String, throwable: Throwable? = null) {
            if (BuildConfig.DEBUG) {
                Log.w(tag, msg, throwable)
            }
        }
    }
}

internal class StytchFlowManager {

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

            val response = when (Stytch.instance.loginMethod) {
                StytchLoginMethod.LoginOrSignUp -> Api.instance.loginOrSignUp(email)
                StytchLoginMethod.LoginOrInvite -> Api.instance.loginOrInvite(email)
            }

            if (response.body() == null)
                throw UnknownException()
            withContext(Dispatchers.Main) {
//                TODO: ????
                if (newUserCreated) {
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
            if (Stytch.instance.config?.verifyEmail == true) {
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
            is UnauthorizedCredentialsException -> {
                withContext(Dispatchers.Main) {
                    Stytch.instance.listener?.onFailure(StytchError.InvalidConfiguration)
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
