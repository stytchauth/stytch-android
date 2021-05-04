package com.stytch.sdk

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.stytch.sdk.helpers.dp
import java.lang.RuntimeException

public class StytchUICustomization {
    var buttonCornerRadius: Float = 5.dp
    var buttonBackgroundColor: StytchColor = StytchColor.fromColorId(R.color.buttonBg)

    var inputBackgroundColor: StytchColor =
        StytchColor.fromColorId(R.color.editTextBackgroundColor)
    var inputBackgroundBorderColor: StytchColor =
        StytchColor.fromColorId(R.color.editTextBackgroundBorderColor)
    var inputCornerRadius: Float = 5.dp

    var backgroundColor: StytchColor = StytchColor.fromColorId(R.color.colorBackground)
    var showBrandLogo: Boolean = true

    var showTitle: Boolean = true
    var showSubtitle: Boolean = true

    var titleStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.titleTextColor)
        font = Typeface.create(null as Typeface?, Typeface.BOLD)
        size = 30.dp
    }

    var subtitleStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.subtitleTextColor)
        font = Typeface.create(null as Typeface?, Typeface.NORMAL)
        size = 16.dp
    }

    var inputTextStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.editTextColor)
        size = 16.dp
    }

    var inputHintStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.editHintTextColor)
        size = 16.dp
    }

    var buttonTextStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.buttonTextColor)
        font = Typeface.create(null as Typeface?, Typeface.BOLD)
        size = 16.dp
    }

}

public class StytchColor private constructor(
    private val colorId: Int? = null,
    private val color: Int? = null,
) {

    @ColorInt
    fun getColor(context: Context): Int {
        if (colorId != null) {
            return ContextCompat.getColor(context, colorId)
        }
        if (color != null) {
            return color
        }

        throw RuntimeException("StytchColor bad status. Please check color initialization.")
    }

    companion object {
        fun fromColorId(@ColorRes colorId: Int): StytchColor {
            return StytchColor(colorId = colorId)
        }

        fun fromColor(@ColorInt color: Int): StytchColor {
            return StytchColor(color = color)
        }
    }
}
