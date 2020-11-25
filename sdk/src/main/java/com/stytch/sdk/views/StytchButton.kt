package com.stytch.sdk.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.stytch.sdk.R
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchTextCustomization
import com.stytch.sdk.StytchUI

class StytchButton constructor(
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
        backgroundTintList = ContextCompat.getColorStateList(context, Stytch.instance.config.customization.buttonBackgroundColorId)
    }

    fun setCustomization(customization: StytchTextCustomization) {
        setTextColor(ContextCompat.getColor(context, customization.colorId))
        setTypeface(customization.font)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, customization.size)
    }

}