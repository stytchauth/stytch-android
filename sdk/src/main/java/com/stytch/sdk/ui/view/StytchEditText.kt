package com.stytch.sdk.ui.view

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.TypefaceSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatEditText
import com.stytch.sdk.R
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchTextStyle
import com.stytch.sdk.dp

internal class StytchEditText @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle,
    forEmailAddress: Boolean = true,
) : AppCompatEditText(context, attributeSet, defStyleAttr) {

    private var hintStyle: StytchTextStyle? = null

    init {
        setPadding(16.dp.toInt(), 16.dp.toInt(), 16.dp.toInt(), 16.dp.toInt())
        Stytch.instance.config?.let { config ->
            background = getBackgroundShape(
                config.uiCustomization.inputBackgroundColor.getColor(context),
                config.uiCustomization.inputBackgroundBorderColor.getColor(context),
                config.uiCustomization.inputCornerRadius,
                1.dp.toInt(),
            )
        }
        if (forEmailAddress) {
            inputType = EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
    }

    fun setHintCustomization(style: StytchTextStyle) {
        hintStyle = style
        setHintTextColor(style.color.getColor(context))
    }

    fun setTextCustomization(style: StytchTextStyle) {
        setTextColor(style.color.getColor(context))
        typeface = style.font
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

        hint = spannableText
    }

    fun setCursorDrawableColor(@ColorInt color: Int) {
//        TODO: change cursor color
    }

    private fun getBackgroundShape(
        backgroundColor: Int,
        borderColor: Int,
        borderRadius: Float,
        borderWidth: Int,
    ): Drawable? {
        val drawable = GradientDrawable()
        drawable.cornerRadius = borderRadius
        drawable.setStroke(borderWidth, borderColor)
        drawable.colors = intArrayOf(backgroundColor, backgroundColor)
        return drawable
    }

    class CustomTypefaceSpan(family: String?, private val newType: Typeface) : TypefaceSpan(family) {
        override fun updateDrawState(ds: TextPaint) {
            applyCustomTypeFace(ds, newType)
        }

        override fun updateMeasureState(paint: TextPaint) {
            applyCustomTypeFace(paint, newType)
        }

        companion object {
            private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
                val oldStyle: Int
                val old = paint.typeface
                oldStyle = old?.style ?: 0
                val fake = oldStyle and tf.style.inv()
                if (fake and Typeface.BOLD != 0) {
                    paint.isFakeBoldText = true
                }
                if (fake and Typeface.ITALIC != 0) {
                    paint.textSkewX = -0.25f
                }
                paint.typeface = tf
            }
        }
    }
}