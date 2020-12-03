package com.stytch.sdk.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.stytch.sdk.StytchTextStyle

class StytchTextView  @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr){

    fun setCustomization(style: StytchTextStyle) {
        setTextColor(ContextCompat.getColor(context, style.colorId))
        setTypeface(style.font)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, style.size)
    }

}