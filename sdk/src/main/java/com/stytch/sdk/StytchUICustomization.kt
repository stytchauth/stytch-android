package com.stytch.sdk

import android.graphics.Typeface
import com.stytch.sdk.helpers.dp

public class StytchUICustomization {
    var buttonCornerRadius: Float = 5.dp
    var buttonBackgroundColorId: Int = R.color.buttonBg

    var inputBackgroundColorId: Int = R.color.editTextBackgroundColor
    var inputBackgroundBorderColorId: Int = R.color.editTextBackgroundBorderColor
    var inputCornerRadius: Float = 5.dp

    var backgroundId: Int = R.color.colorBackground
    var showBrandLogo: Boolean = true

    var showTitle: Boolean = true
    var showSubtitle: Boolean = true

    var titleStyle: StytchTextStyle = StytchTextStyle().apply {
        colorId = R.color.titleTextColor
        font = Typeface.create(null as Typeface?, Typeface.BOLD)
        size = 30.dp
    }

    var subtitleStyle: StytchTextStyle = StytchTextStyle().apply {
        colorId = R.color.subtitleTextColor
        font = Typeface.create(null as Typeface?, Typeface.NORMAL)
        size = 16.dp
    }

    var inputTextStyle: StytchTextStyle = StytchTextStyle().apply {
        colorId = R.color.editTextColor
        size = 16.dp
    }

    var inputHintStyle: StytchTextStyle = StytchTextStyle().apply {
        colorId = R.color.editHintTextColor
        size = 16.dp
    }

    var buttonTextStyle: StytchTextStyle = StytchTextStyle().apply {
        colorId = R.color.buttonTextColor
        font = Typeface.create(null as Typeface?, Typeface.BOLD)
        size = 16.dp
    }

}