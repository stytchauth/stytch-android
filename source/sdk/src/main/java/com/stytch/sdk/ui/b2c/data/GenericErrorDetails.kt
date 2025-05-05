package com.stytch.sdk.ui.b2c.data

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.stytch.sdk.ui.shared.components.FormFieldStatus
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class GenericErrorDetails(
    val errorText: String? = null,
    val errorMessageId: Int? = null,
    val arguments: List<String> = emptyList(),
) : Parcelable {
    @Composable
    fun getText(): String? =
        errorMessageId?.let {
            stringResource(it, *arguments.toTypedArray())
        } ?: errorText

    @Composable
    fun display() {
        getText()?.let {
            FormFieldStatus(text = it, isError = true)
        }
    }
}
