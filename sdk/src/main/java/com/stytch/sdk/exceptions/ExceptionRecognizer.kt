package com.stytch.sdk.exceptions

import com.google.gson.Gson
import com.stytch.sdk.R
import com.stytch.sdk.api.responses.BasicErrorResponse
import com.stytch.sdk.api.responses.BasicResponse
import com.stytch.sdk.helpers.LoggerLocal
import retrofit2.Response
import java.net.UnknownHostException

private const val TAG = "ExceptionRecognizer"

object ExceptionRecognizer {

    //    TODO:("use parmas")
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
                else -> {
                    throw BlankWarningException(errorModel.error_type)
//                    throw WarningException(R.string.error_unknown)
                }
            }
        }

        response.body()?.let {
            LoggerLocal.d(TAG, "response.body() != null")
            when (it) {
                is BasicResponse -> {

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