package com.stytch.sdk

import android.graphics.Typeface
import com.stytch.sdk.helpers.dp

class StytchCustomization {
    var buttonBackgroundColorId: Int = R.color.buttonBg
    var editBackgroundColorId: Int = R.color.editTextBackgroundBorderColor
    var backgroundId: Int = R.color.colorBackground
    var showBrandLogo: Boolean = true

    var titleCustomization: StytchTextCustomization = StytchTextCustomization().apply {
        colorId = R.color.titleTextColor
        font = Typeface.create(null as Typeface?, Typeface.BOLD)
        size = 30.dp
    }

    var subtitleCustomization: StytchTextCustomization = StytchTextCustomization().apply {
        colorId = R.color.titleTextColor
        font = Typeface.create(null as Typeface?, Typeface.NORMAL)
        size = 16.dp
    }

    var editTextCustomization: StytchTextCustomization = StytchTextCustomization().apply {
        colorId = R.color.editTextColor
        size = 16.dp
    }

    var editHintCustomization: StytchTextCustomization = StytchTextCustomization().apply {
        colorId = R.color.editHintTextColor
        size = 16.dp
    }

    var buttonTextCustomization: StytchTextCustomization = StytchTextCustomization().apply {
        colorId = R.color.buttonTextColor
        font = Typeface.create(null as Typeface?, Typeface.BOLD)
        size = 16.dp
    }

}