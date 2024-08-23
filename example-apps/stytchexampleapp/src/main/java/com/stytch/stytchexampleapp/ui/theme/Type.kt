package com.stytch.stytchexampleapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.stytch.stytchexampleapp.R

// Set of Material typography styles to start with
val Typography =
    Typography(
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.ibm_plex_sans_regular)),
                fontWeight = FontWeight.W600,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Start,
                color = Color.Black,
            ),
        bodySmall =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.ibm_plex_mono_regular)),
                fontWeight = FontWeight.W400,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Start,
                color = Color.Black,
            ),
    )
