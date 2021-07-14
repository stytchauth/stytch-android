package com.stytch.sdk

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import android.support.annotation.FontRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

public object StytchUI {
    private var _uiCustomization: StytchUICustomization? = null
    public var uiCustomization: StytchUICustomization
        set(value) {
            _uiCustomization = value
        }
        get() {
            if (_uiCustomization == null) uiCustomization = StytchUICustomization()
            return _uiCustomization!!
        }

    public object EmailMagicLink {
        internal var configured = false
        internal lateinit var loginMagicLinkUrl: String
        internal lateinit var signupMagicLinkUrl: String
        internal var createUserAsPending = false
        internal lateinit var authenticator: Authenticator

        @JvmStatic
        public fun configure(
            loginMagicLinkUrl: String,
            signupMagicLinkUrl: String,
            createUserAsPending: Boolean,
            authenticator: Authenticator,
        ) {
            configured = true
            this.loginMagicLinkUrl = loginMagicLinkUrl
            this.signupMagicLinkUrl = signupMagicLinkUrl
            this.createUserAsPending = createUserAsPending
            this.authenticator = authenticator
        }

        @JvmStatic
        public fun createIntent(appContext: Context): Intent {
            return stytchIntent(appContext, StytchEmailMagicLinkActivity::class.java)
        }

        public fun interface Authenticator {
            /**
             * Once the magic link is authenticated,
             * you must call [StytchUI.onTokenAuthenticated] with either true (if token was successfully authenticated)
             * or false (if token authentication failed).
             */
            public fun authenticateToken(token: String)
        }
    }

    public object SMSPasscode {
        internal var configured = false
        internal var createUserAsPending = false
        internal lateinit var authenticator: Authenticator

        @JvmStatic
        public fun configure(
            createUserAsPending: Boolean,
            authenticator: Authenticator,
        ) {
            configured = true
            this.createUserAsPending = createUserAsPending
            this.authenticator = authenticator
        }

        @JvmStatic
        public fun createIntent(appContext: Context): Intent {
            return stytchIntent(appContext, StytchSMSPasscodeActivity::class.java)
        }

        public fun interface Authenticator {
            /**
             * Once the magic link is authenticated,
             * you must call [StytchUI.onTokenAuthenticated] with either true (if token was successfully authenticated)
             * or false (if token authentication failed).
             */
            public fun authenticateToken(methodId: String, token: String)
        }
    }

    @JvmStatic
    public fun onTokenAuthenticated(success: Boolean) {
        val currentScreen = StytchActivity.navigator?.currentScreen() as? StytchScreen
        currentScreen?.let {
            if (success) {
                it.getActivity().finish()
            } else {
                it.onAuthenticationError()
            }
        }
    }
}

public class StytchUICustomization(
    public var backgroundColor: StytchColor = StytchColor.fromColorId(R.color.backgroundColor),
    public var hideActionBar: Boolean = false,
    public var actionBarColor: StytchColor = StytchColor.fromColorId(R.color.tertiaryBrandColor),
    public var titleStyle: StytchTextStyle = StytchTextStyle(
        color = StytchColor.fromColorId(R.color.primaryBrandColor),
        font = StytchFont.fromFontId(R.font.ibm_plex_sans, style = StytchFont.Style.BOLD),
        size = 30.sp,
    ),
    public var subtitleStyle: StytchTextStyle = StytchTextStyle(
        color = StytchColor.fromColorId(R.color.primaryBrandColor),
        size = 16.sp,
    ),
    public var smsConsentTextStyle: StytchTextStyle = StytchTextStyle(
        color = StytchColor.fromColorId(R.color.primaryBrandColor),
        size = 14.sp,
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
    ),
) {
    public companion object {
        /**
         * Convenience method for use from Java, builds a default customization.
         * Fields can then be customized directly.
         */
        @JvmStatic
        public fun createDefault(): StytchUICustomization {
            return StytchUICustomization()
        }
    }
}

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
public value class ScalablePixels constructor(internal val pixels: Float) {
    internal fun toFloat(): Float = pixels * Resources.getSystem().displayMetrics.scaledDensity
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
                Style.NORMAL -> baseFont
                Style.BOLD -> Typeface.create(baseFont, Typeface.BOLD)
                Style.ITALIC -> Typeface.create(baseFont, Typeface.ITALIC)
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

internal fun <T : StytchActivity> stytchIntent(context: Context, activity: Class<T>): Intent {
    return Intent(context, activity).apply {
        putExtra(StytchActivity.ACTIVITY_ID_EXTRA_NAME, StytchActivity.getNextActivityId())
    }
}
