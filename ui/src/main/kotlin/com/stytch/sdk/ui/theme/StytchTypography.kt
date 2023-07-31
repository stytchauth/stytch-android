package com.stytch.sdk.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.stytch.sdk.ui.R

internal data class StytchTypography(
    val title: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.ibm_plex_sans_regular)),
        fontWeight = FontWeight.W600,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        textAlign = TextAlign.Start,
        color = Color.Black,
    ),
    val body: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.ibm_plex_sans_regular)),
        fontWeight = FontWeight.W700,
        fontSize = 18.sp,
        lineHeight = 25.sp,
        textAlign = TextAlign.Start,
        color = Color.Black,
    ),
    val buttonLabel: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.ibm_plex_sans_regular)),
        fontWeight = FontWeight.W600,
        fontSize = 18.sp,
        lineHeight = 31.5.sp,
        textAlign = TextAlign.Center,
        color = Color.Black,
    ),
    val caption: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.ibm_plex_sans_regular)),
        fontWeight = FontWeight.W600,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        textAlign = TextAlign.Start,
        color = Color.Black,
    ),
    val tab: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        textAlign = TextAlign.Center,
        color = Color.Black,
        letterSpacing = 0.1.sp,
    ),
)
