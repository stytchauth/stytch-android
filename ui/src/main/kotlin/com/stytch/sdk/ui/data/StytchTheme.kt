package com.stytch.sdk.ui.data

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.io.File
import kotlinx.parcelize.Parcelize

private val CHARCOAL: Int = Color(0xFF19303D).toArgb()
private val BLACK: Int = Color(0xFF000000).toArgb()
private val WHITE: Int = Color(0xFFFFFFFF).toArgb()
private val SLATE: Int = Color(0xFFFF5C727D).toArgb()
private val PINE: Int = Color(0xFFFF0C5A56).toArgb()
private val MAROON: Int = Color(0xFFFF8B1214).toArgb()
private val STEEL: Int = Color(0xFFFF8296A1).toArgb()
private val CEMENT: Int = Color(0xFFFFADBCC5).toArgb()
private val CHALK: Int = Color(0xFFFFF3F5F6).toArgb()
private val FOG: Int = Color(0xFFFFE5E8EB).toArgb()
private val MINT: Int = Color(0xFFFFC6FFE0).toArgb()
private val PEACH: Int = Color(0xFFFFFFD4CD).toArgb()
private val INK: Int = Color(0xFFFF354D5A).toArgb()

/*
    StytchTheme defaults to Light Colors
 */
@Parcelize
public data class StytchTheme(
    val backgroundColor: Int = WHITE,
    val primaryTextColor: Int = BLACK,
    val secondaryTextColor: Int = SLATE,
    val disabledTextColor: Int = STEEL,
    val successColor: Int = PINE,
    val errorColor: Int = MAROON,
    val socialButtonBackgroundColor: Int = WHITE,
    val socialButtonTextColor: Int = CHARCOAL,
    val buttonBackgroundColor: Int = CHARCOAL,
    val buttonTextColor: Int = WHITE,
    val buttonBorderColor: Int = CHARCOAL,
    val buttonBorderRadius: Int = 4,
    val disabledButtonBackgroundColor: Int = CHALK,
    val disabledButtonBorderColor: Int = CHALK,
    val disabledButtonTextColor: Int = STEEL,
    val inputBorderRadius: Int = 4,
    val inputBorderColor: Int = CEMENT,
    val inputBackgroundColor: Int = WHITE,
    val inputTextColor: Int = CHARCOAL,
    val inputPlaceholderTextColor: Int = STEEL,
    val disabledInputBorderColor: Int = FOG,
    val disabledInputBackgroundColor: Int = CHALK,
    val disabledInputTextColor: Int = STEEL,
    val hideHeaderText: Boolean = false,
    val logoImageUrl: File? = null,
) : Parcelable {
    internal companion object {
        fun defaultDarkTheme(): StytchTheme = StytchTheme(
            backgroundColor = CHARCOAL,
            primaryTextColor = WHITE,
            secondaryTextColor = CEMENT,
            successColor = MINT,
            errorColor = PEACH,
            buttonBackgroundColor = WHITE,
            buttonTextColor = CHARCOAL,
            buttonBorderColor = WHITE,
            disabledButtonBackgroundColor = INK,
            disabledButtonBorderColor = INK,
            inputBorderColor = SLATE,
            inputBackgroundColor = CHARCOAL,
            inputTextColor = WHITE,
            disabledInputBorderColor = INK,
            disabledInputBackgroundColor = INK,
        )
        fun defaultLightTheme(): StytchTheme = StytchTheme()
    }
}
