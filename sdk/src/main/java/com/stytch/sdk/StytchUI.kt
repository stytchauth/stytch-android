package com.stytch.sdk

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import java.lang.RuntimeException

public class StytchUI private constructor() {

    public var uiListener: StytchUIListener? = null

    // TODO ??
    public var uiCustomization: StytchUICustomization = StytchUICustomization()
        set(value) {
            try {
                Stytch.instance.config!!.uiCustomization = value
            } catch (ex: Exception) {
                throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
            }
        }

    public interface StytchUIListener {
        public fun onEvent(event: StytchEvent) {}
        public fun onSuccess(result: StytchResult)
        public fun onFailure()
    }

    public companion object {
        private const val ACTIVITY_REQUEST_CODE = 674

        public val instance: StytchUI = StytchUI()
    }
}

public class StytchUICustomization {
    public var buttonCornerRadius: Float = 5.dp
    public var buttonBackgroundColor: StytchColor = StytchColor.fromColorId(R.color.buttonBg)

    public var inputBackgroundColor: StytchColor =
        StytchColor.fromColorId(R.color.editTextBackgroundColor)
    public var inputBackgroundBorderColor: StytchColor =
        StytchColor.fromColorId(R.color.editTextBackgroundBorderColor)
    public var inputCornerRadius: Float = 5.dp

    public var backgroundColor: StytchColor = StytchColor.fromColorId(R.color.colorBackground)
    public var showBrandLogo: Boolean = true

    public var showTitle: Boolean = true
    public var showSubtitle: Boolean = true

    public var titleStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.titleTextColor)
        font = Typeface.create(null as Typeface?, Typeface.BOLD)
        size = 30.dp
    }

    public var subtitleStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.subtitleTextColor)
        font = Typeface.create(null as Typeface?, Typeface.NORMAL)
        size = 16.dp
    }

    public var inputTextStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.editTextColor)
        size = 16.dp
    }

    public var inputHintStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.editHintTextColor)
        size = 16.dp
    }

    public var buttonTextStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.buttonTextColor)
        font = Typeface.create(null as Typeface?, Typeface.BOLD)
        size = 16.dp
    }

}

public val Number.px: Float
    get() = this.toFloat() / Resources.getSystem().displayMetrics.density

public val Number.dp: Float
    get() = this.toFloat() * Resources.getSystem().displayMetrics.density

public class StytchColor private constructor(
    private val colorId: Int? = null,
    private val color: Int? = null,
) {

    @ColorInt
    public fun getColor(context: Context): Int {
        if (colorId != null) {
            return ContextCompat.getColor(context, colorId)
        }
        if (color != null) {
            return color
        }

        throw RuntimeException("StytchColor bad status. Please check color initialization.")
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

public class StytchTextStyle {
    public var size: Float = 10.dp
    public var color: StytchColor = StytchColor.fromColorId(R.color.editTextColor)
    public var font: Typeface? = null
}
