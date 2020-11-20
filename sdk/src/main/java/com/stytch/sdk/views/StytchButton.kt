package com.stytch.sdk.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.stytch.sdk.R

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
    }

}