package com.stytch.sdk

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.support.annotation.FontRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

public class StytchUICustomization(
    public var backgroundColor: StytchColor = StytchColor.fromColorId(R.color.backgroundColor),
    public var hideActionBar: Boolean = false,
    public var actionBarColor: StytchColor = StytchColor.fromColorId(R.color.tertiaryBrandColor),
    public var showTitle: Boolean = true,
    public var titleStyle: StytchTextStyle = StytchTextStyle(
        color = StytchColor.fromColorId(R.color.primaryBrandColor),
        font = StytchFont.fromFontId(R.font.ibm_plex_sans, style = StytchFont.Style.BOLD),
        size = 30.sp,
    ),
    public var showSubtitle: Boolean = true,
    public var subtitleStyle: StytchTextStyle = StytchTextStyle(
        color = StytchColor.fromColorId(R.color.primaryBrandColor),
        size = 16.sp,
    ),
    public var inputTextStyle: StytchTextStyle = StytchTextStyle(
        color = StytchColor.fromColorId(R.color.primaryBrandColor),
        size = 16.sp,
    ),
    public var inputHintStyle: StytchTextStyle = StytchTextStyle(
        color = StytchColor.fromColorId(R.color.secondaryBrandColor),
        size = 16.sp,
    ),
    public var inputBackgroundColor: StytchColor = StytchColor.fromColorId(R.color.backgroundColor),
    public var inputBackgroundBorderColor: StytchColor = StytchColor.fromColorId(R.color.secondaryBrandColor),
    public var inputCornerRadius: DensityIndependentPixels = 5.dp,
    public var buttonTextStyle: StytchTextStyle = StytchTextStyle(
        color = StytchColor.fromColorId(R.color.buttonTextColor),
        font = StytchFont.fromFontId(R.font.ibm_plex_sans, style = StytchFont.Style.BOLD),
        size = 16.sp,
    ),
    public var buttonDisabledTextColor: StytchColor = StytchColor.fromColorId(R.color.fourthBrandColor),
    public var buttonEnabledBackgroundColor: StytchColor = StytchColor.fromColorId(R.color.primaryBrandColor),
    public var buttonDisabledBackgroundColor: StytchColor = StytchColor.fromColorId(R.color.fifthBrandColor),
    public var buttonCornerRadius: DensityIndependentPixels = 5.dp,
    public var errorTextStyle: StytchTextStyle = StytchTextStyle(
        color = StytchColor.fromColorId(R.color.errorTextColor),
        size = 14.sp,
    )
)

public class StytchTextStyle(
    public var size: ScalablePixels = 10.sp,
    public var color: StytchColor = StytchColor.fromColorId(R.color.primaryBrandColor),
    public var font: StytchFont = StytchFont.fromFontId(R.font.ibm_plex_sans),
)

@JvmInline
public value class DensityIndependentPixels(private val pixels: Float) {
    internal fun toFloat(): Float = pixels * Resources.getSystem().displayMetrics.density
}

@JvmInline
public value class ScalablePixels(private val pixels: Float) {
    internal fun toFloat(): Float = pixels // Resources.getSystem().displayMetrics.density // TODO adjust for font size
}

public val Number.dp: DensityIndependentPixels get() = DensityIndependentPixels(this.toFloat())
public val Number.sp: ScalablePixels get() = ScalablePixels(this.toFloat())

public class StytchColor internal constructor(
    internal val colorId: Int? = null,
    internal val color: Int? = null,
) {

    @ColorInt
    public fun getColor(context: Context): Int {
        return color ?: ContextCompat.getColor(context, colorId!!)
    }

    public companion object {
        public fun fromColorId(@ColorRes colorId: Int): StytchColor {
            return StytchColor(colorId = colorId)
        }

        public fun fromColor(@ColorInt color: Int): StytchColor {
            return StytchColor(color = color)
        }
    }
}

public class StytchFont private constructor(
    @FontRes private val fontId: Int? = null,
    private val typeface: Typeface? = null,
    private val style: Style = Style.NORMAL,
) {

    public fun getFont(context: Context): Typeface? {
        return if (fontId != null) {
            val baseFont = ResourcesCompat.getFont(context, fontId)
            when (style) {
                Style.NORMAL      -> baseFont
                Style.BOLD        -> Typeface.create(baseFont, Typeface.BOLD)
                Style.ITALIC      -> Typeface.create(baseFont, Typeface.ITALIC)
                Style.BOLD_ITALIC -> Typeface.create(baseFont, Typeface.BOLD_ITALIC)
            }
        } else typeface
    }

    public enum class Style {
        NORMAL,
        BOLD,
        ITALIC,
        BOLD_ITALIC
    }

    public companion object {
        public fun fromFontId(@FontRes fontId: Int, style: Style = Style.NORMAL): StytchFont {
            return StytchFont(fontId = fontId, style = style)
        }

        public fun fromTypeface(typeface: Typeface?): StytchFont {
            return StytchFont(typeface = typeface)
        }
    }
}
