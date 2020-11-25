package com.stytch.sdk.views

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.stytch.sdk.R
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchTextCustomization
import com.stytch.sdk.StytchUI
import com.stytch.sdk.helpers.CustomTypefaceSpan
import com.stytch.sdk.helpers.dp

class StytchEditText(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
    AppCompatEditText(context, attributeSet, defStyleAttr) {

    private var hintCustomization: StytchTextCustomization? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        android.R.attr.editTextStyle
    )

    init {
        setBackgroundResource(R.drawable.stytch_edit_bg)
        setPadding(16.dp.toInt(), 16.dp.toInt(), 16.dp.toInt(), 16.dp.toInt())
        backgroundTintList = ContextCompat.getColorStateList(
            context,
            Stytch.instance.config.customization.editBackgroundColorId
        )
    }

    fun setHintCustomization(customization: StytchTextCustomization) {
        hintCustomization = customization
        setHintTextColor(ContextCompat.getColor(context, customization.colorId))
    }

    fun setTextCustomization(customization: StytchTextCustomization) {
        setTextColor(ContextCompat.getColor(context, customization.colorId))
        setTypeface(customization.font)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, customization.size)
        setCursorDrawableColor(customization.colorId)
    }

    fun updateHint(textId: Int) {
        val text = context.getString(textId)
        val spannableText = SpannableString(text)

        hintCustomization?.let { customization ->

            customization.font?.let { font ->
                spannableText.setSpan(
                    CustomTypefaceSpan(null, font),
                    0,
                    text.length,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }

            spannableText.setSpan(
                AbsoluteSizeSpan(customization.size.toInt(), false),
                0,
                text.length,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )

        }

        setHint(spannableText)
    }

    fun setCursorDrawableColor(@ColorInt color: Int) {
//        TODO: change cursor color
    }


}