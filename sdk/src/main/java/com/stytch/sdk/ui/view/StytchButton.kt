package com.stytch.sdk.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.stytch.sdk.R
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchTextStyle

internal class StytchButton constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.buttonStyle,
) : AppCompatButton(context, attrs, defStyleAttr) {

    init {
        setTextColor(ContextCompat.getColor(context, R.color.buttonTextColor))
        setBackgroundResource(R.drawable.stytch_button_bg_rounded)
        isAllCaps = false
        Stytch.instance.config?.let { config ->
            val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled),
            )

            val color = config.uiCustomization.buttonBackgroundColor.getColor(context)

            val colors = intArrayOf(
                color,
                color
            )
            backgroundTintList = ColorStateList(states, colors);
        }

        Stytch.instance.config?.let { config ->
            background = getBackgroundShape(
                config.uiCustomization.buttonBackgroundColor.getColor(
                    context
                ),
                config.uiCustomization.buttonCornerRadius
            )
        }

    }

    fun setCustomization(style: StytchTextStyle) {
        setTextColor(style.color.getColor(context))
        typeface = style.font
        setTextSize(TypedValue.COMPLEX_UNIT_PX, style.size)
    }

    private fun getBackgroundShape(backgroundColor: Int, borderRadius: Float): Drawable? {
        val drawable = GradientDrawable()
        drawable.cornerRadius = borderRadius
        drawable.colors = intArrayOf(backgroundColor, backgroundColor)
        return drawable
    }

}
