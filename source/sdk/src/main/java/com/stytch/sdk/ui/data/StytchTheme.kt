package com.stytch.sdk.ui.data

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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

/**
 * A data class used to configure the UI.
 * @param backgroundColor an Int describing the background color
 * @param primaryTextColor an Int describing the color of primary text
 * @param secondaryTextColor an Int describing the color of secondary text
 * @param disabledTextColor an Int describing the color of text when in a disabled state
 * @param successColor an Int describing the color of text and fields in a success state
 * @param errorColor an Int describing the color of text and fields in an error state
 * @param socialButtonBackgroundColor an Int describing the background color of Social Login buttons
 * @param socialButtonTextColor an Int describing the text color of Social Login buttons
 * @param buttonBackgroundColor an Int describing the background color of a button
 * @param buttonTextColor an Int describing the color of button text
 * @param buttonBorderColor an Int describing the border color of a button
 * @param buttonBorderRadius an Int describing the size of a button's border radius
 * @param disabledButtonBackgroundColor an Int describing the color of a button's background when disabled
 * @param disabledButtonBorderColor an Int describing the color of a button's border when disabled
 * @param disabledButtonTextColor an Int describing the color of button text when in a disabled state
 * @param inputBorderRadius an Int describing the size of an input's border radius
 * @param inputBorderColor an Int describing the border color of input fields
 * @param inputBackgroundColor an Int describing the background color of input fields
 * @param inputTextColor an Int describing the color of input text when editable
 * @param inputPlaceholderTextColor an Int describing the text color of placeholder text
 * @param disabledInputBorderColor an Int describing the color of an input fields border when disabled
 * @param disabledInputBackgroundColor an Int describing the color of an input fields background when disabled
 * @param disabledInputTextColor an Int describing the color of input text when in a disabled state
 * @param dialogTextColor an Int describing the color of text within dialogs
 * @param hideHeaderText a Boolean that determines whether or not to show the header text on the main UI screen.
 */
@Parcelize
public data class StytchTheme
    @JvmOverloads
    constructor(
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
        val dialogTextColor: Int = CHARCOAL,
        val hideHeaderText: Boolean = false,
    ) : Parcelable {
        internal companion object {
            fun defaultDarkTheme(): StytchTheme =
                StytchTheme(
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
