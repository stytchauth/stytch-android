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
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchTextStyle
import com.stytch.sdk.helpers.CustomTypefaceSpan
import com.stytch.sdk.helpers.dp

internal class StytchEditText(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
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
        Stytch.instance.config?.let { config ->
            background = getBackgroundShape(
                config.uiCustomization.inputBackgroundColor.getColor(context),
                config.uiCustomization.inputBackgroundBorderColor.getColor(context),
                config.uiCustomization.inputCornerRadius,
                1.dp.toInt()
            )
        }
    }

    fun setHintCustomization(style: StytchTextStyle) {
        hintStyle = style
        setHintTextColor(style.color.getColor(context))
    }

    fun setTextCustomization(style: StytchTextStyle) {
        setTextColor(style.color.getColor(context))
        setTypeface(style.font)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, style.size)
        setCursorDrawableColor(style.color.getColor(context))
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