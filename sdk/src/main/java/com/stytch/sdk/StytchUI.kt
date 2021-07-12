package com.stytch.sdk

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import android.support.annotation.FontRes
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import java.io.Serializable

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
        public fun activityLauncher(
            context: ActivityResultCaller,
            onResult: (StytchUIResult) -> Unit,
        ): StytchActivityLauncher {
            return StytchActivityLauncher(context.registerForActivityResult(activityResultContract, onResult))
        }

        private val activityResultContract by lazy {
            object : ActivityResultContract<Unit, StytchUIResult>() {
                override fun createIntent(context: Context, input: Unit): Intent {
                    return this@EmailMagicLink.createIntent(context)
                }

                override fun parseResult(resultCode: Int, intent: Intent?): StytchUIResult {
                    return intent.toStytchUIResult()
                }
            }
        }

        @JvmStatic
        public fun createIntent(appContext: Context): Intent {
            return stytchIntent(appContext, StytchEmailMagicLinkActivity::class.java)
        }

        public abstract class Authenticator {
            internal lateinit var callback: (Boolean) -> Unit

            public fun onComplete(success: Boolean) {
                callback(success)
            }

            /**
             * Once the magic link is authenticated,
             * you must call [onComplete] with either true (if token was successfully authenticated)
             * or false (if token authentication failed).
             */
            public abstract fun authenticateToken(token: String)
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
        public fun activityLauncher(
            context: ActivityResultCaller,
            onResult: (StytchUIResult) -> Unit,
        ): StytchActivityLauncher {
            return StytchActivityLauncher(context.registerForActivityResult(activityResultContract, onResult))
        }

        private val activityResultContract by lazy {
            object : ActivityResultContract<Unit, StytchUIResult>() {
                override fun createIntent(context: Context, input: Unit): Intent {
                    return this@SMSPasscode.createIntent(context)
                }

                override fun parseResult(resultCode: Int, intent: Intent?): StytchUIResult {
                    return intent.toStytchUIResult()
                }
            }
        }

        @JvmStatic
        public fun createIntent(appContext: Context): Intent {
            return stytchIntent(appContext, StytchSMSPasscodeActivity::class.java)
        }

        public abstract class Authenticator {
            internal lateinit var callback: (Boolean) -> Unit

            public fun onComplete(success: Boolean) {
                callback(success)
            }

            /**
             * Once the magic link is authenticated,
             * you must call [callback] with either true (if token was successfully authenticated)
             * or false (if token authentication failed).
             */
            public abstract fun authenticateToken(methodId: String, token: String)
        }
    }

    internal fun onTokenAuthenticated(success: Boolean) {
        val currentScreen = StytchActivity.navigator?.currentScreen() as? StytchScreen
        currentScreen?.let {
            if (success) {
                it.getActivity().finishSuccessfullyWithResult(true)
            } else {
                it.onAuthenticationError()
            }
        }
    }
}

public sealed class StytchUIResult {
    public object Success : StytchUIResult()
    public object CancelledByUser : StytchUIResult()
}

public fun Intent?.toStytchUIResult(): StytchUIResult {
    disposeAllScreens()
    return when (this) {
        null -> StytchUIResult.CancelledByUser
        else -> StytchUIResult.Success
    }
}

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
    public var consentTextStyle: StytchTextStyle = StytchTextStyle(
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

internal inline fun <reified T : Serializable> intentWithExtra(extra: T): Intent {
    return Intent().withSerializableExtra(extra)
}

internal inline fun <reified T : Serializable> Intent.withSerializableExtra(extra: T): Intent = apply {
    putExtra(T::class.qualifiedName, extra)
}

internal inline fun <reified T : Serializable> Intent?.getSerializableExtra(): T? {
    return this?.extras?.getSerializable(T::class.qualifiedName) as? T
}

internal fun disposeAllScreens() {
    StytchActivity.navigator = null
}
