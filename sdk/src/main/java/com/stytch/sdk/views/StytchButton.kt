package com.stytch.sdk.views

import android.content.Context
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
    context: Context, attrs: AttributeSet?, defStyleAttr: Int
) : AppCompatButton(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        android.R.attr.buttonStyle
    )

    init {
        setTextColor(ContextCompat.getColor(context, R.color.buttonTextColor))
        setBackgroundResource(R.drawable.stytch_button_bg_rounded)
        isAllCaps = false
        Stytch.instance.config?.let { config ->
            backgroundTintList = ContextCompat.getColorStateList(
                context,
               config.uiCustomization.buttonBackgroundColorId
            )
        }

        Stytch.instance.config?.let { config ->
            background = getBackgroundShape(
                ContextCompat.getColor(
                    context,
                    config.uiCustomization.buttonBackgroundColorId
                ),
                config.uiCustomization.buttonCornerRadius
            )
        }

    }

    fun setCustomization(style: StytchTextStyle) {
        setTextColor(ContextCompat.getColor(context, style.colorId))
        setTypeface(style.font)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, style.size)
    }

    private fun getBackgroundShape(backgroundColor: Int, borderRadius: Float): Drawable? {
        val drawable = GradientDrawable()
        drawable.cornerRadius = borderRadius
        drawable.colors = intArrayOf(backgroundColor, backgroundColor)
        return drawable
    }

}