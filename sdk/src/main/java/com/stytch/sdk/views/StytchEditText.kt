package com.stytch.sdk.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchTextStyle
import com.stytch.sdk.helpers.CustomTypefaceSpan
import com.stytch.sdk.helpers.dp

class StytchEditText(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
    AppCompatEditText(context, attributeSet, defStyleAttr) {

    private var hintStyle: StytchTextStyle? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        android.R.attr.editTextStyle
    )

    init {
        setPadding(16.dp.toInt(), 16.dp.toInt(), 16.dp.toInt(), 16.dp.toInt())
        background = getBackgroundShape(
            ContextCompat.getColor(
                context,
                Stytch.instance.config.uiCustomization.inputBackgroundColorId
            ),
            ContextCompat.getColor(
                context,
                Stytch.instance.config.uiCustomization.inputBackgroundBorderColorId
            ),
            Stytch.instance.config.uiCustomization.inputCornerRadius,
            1.dp.toInt()
        )
    }

    fun setHintCustomization(style: StytchTextStyle) {
        hintStyle = style
        setHintTextColor(ContextCompat.getColor(context, style.colorId))
    }

    fun setTextCustomization(style: StytchTextStyle) {
        setTextColor(ContextCompat.getColor(context, style.colorId))
        setTypeface(style.font)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, style.size)
        setCursorDrawableColor(style.colorId)
    }

    fun updateHint(textId: Int) {
        val text = context.getString(textId)
        val spannableText = SpannableString(text)

        hintStyle?.let { customization ->

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

    private fun getBackgroundShape(
        backgroundColor: Int,
        borderColor: Int,
        borderRadius: Float,
        borderWidth: Int
    ): Drawable? {
        val drawable = GradientDrawable()
        drawable.cornerRadius = borderRadius
        drawable.setStroke(borderWidth, borderColor)
        drawable.colors = intArrayOf(backgroundColor, backgroundColor)
        return drawable
    }


}