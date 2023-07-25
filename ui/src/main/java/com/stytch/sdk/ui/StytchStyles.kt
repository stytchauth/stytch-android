package com.stytch.sdk.ui

import android.graphics.Color
import androidx.core.graphics.toColor
import java.net.URL

private val CHARCOAL: Color = Color.parseColor("#19303D").toColor()
private val WHITE: Color = Color.WHITE.toColor()
private val SLATE: Color = Color.parseColor("#5C727D").toColor()
private val PINE: Color = Color.parseColor("#0C5A56").toColor()
private val MAROON: Color = Color.parseColor("#8B1214").toColor()

public data class StytchStyles(
    val backgroundColor: Color = WHITE,
    val primaryColor: Color = CHARCOAL,
    val secondaryColor: Color = SLATE,
    val successColor: Color = PINE,
    val errorColor: Color = MAROON,
    val primaryButtonBackgroundColor: Color = CHARCOAL,
    val primaryButtonTextColor: Color = WHITE,
    val primaryButtonBorderColor: Color = CHARCOAL,
    val primaryButtonBorderRadius: Int = 3,
    val secondaryButtonBackgroundColor: Color = WHITE,
    val secondaryButtonTextColor: Color = CHARCOAL,
    val secondaryButtonBorderColor: Color = CHARCOAL,
    val secondaryButtonBorderRadius: Int = 3,
    val hideHeaderText: Boolean = false,
    val logoImageUrl: URL? = null,
)
