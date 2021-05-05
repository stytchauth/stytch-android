package com.stytch.sdk

import com.google.gson.Gson
import retrofit2.Response
import java.net.UnknownHostException

internal class BadRequestException : Exception()
internal class BlankWarningException(val errorMessage: String?) : Exception()
internal class EmailNotFoundException : Exception()
internal class UnauthorizedCredentialsException : Exception()
internal class UnknownException : Exception()
internal class WarningException(val errorMessageId: Int) : Exception()
internal class WrongMagicLinkException : Exception()

internal object ExceptionRecognizer {
    private const val TAG = "ExceptionRecognizer"

    fun <T> recognize(response: Response<T>) {
        LoggerLocal.d(TAG, "recognizingError: ${response.body()}")
        val errorModel =
            Gson().fromJson(response.errorBody()?.string(), BasicErrorResponse::class.java)
        if (errorModel?.error_type != null) {
            when (errorModel.error_type) {
                "email_not_found" -> {
                    throw EmailNotFoundException()
                }
                "unable_to_auth_magic_link" -> {
                    throw WrongMagicLinkException()
                }
                "invalid_user_id" -> {
                    throw BlankWarningException(errorModel.error_type)
                }
                "unauthorized_credentials" -> {
                    throw UnauthorizedCredentialsException()
                }
                else -> {
                    throw BlankWarningException(errorModel.error_type)
                }
            }
        }

        when (response.code()) {
            200 -> {
            }
            else -> throw UnknownException()
        }
    }

    fun recognize(t: Throwable, onError: ((Throwable) -> Unit)?) {

        val exception: Throwable =
            when (t) {
                is UnknownHostException -> {
                    WarningException(R.string.stytch_error_no_internet)
                }
                else -> t
            }

        onError?.invoke(exception)
    }
}
