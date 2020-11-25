package com.stytch.sdk.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.stytch.sdk.StytchTextCustomization

class StytchTextView  @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr){

    fun setCustomization(customization: StytchTextCustomization) {
        setTextColor(ContextCompat.getColor(context, customization.colorId))
        setTypeface(customization.font)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, customization.size)
    }

}