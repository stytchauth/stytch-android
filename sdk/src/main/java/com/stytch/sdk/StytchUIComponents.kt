package com.stytch.sdk

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ScrollView
import androidx.annotation.CallSuper
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.stytch.sdk.StytchUI.uiCustomization
import com.wealthfront.magellan.Screen
import com.wealthfront.magellan.ScreenView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
internal abstract class StytchScreen<V : StytchScreenView<*>> : Screen<V>() {
    @CallSuper
    override fun onShow(context: Context?) {
        super.onShow(context)

        view.coroutineScope = CoroutineScope(SupervisorJob()) // TODO
        view.subscribeToState()
    }

    @CallSuper
    override fun onHide(context: Context?) {
        super.onHide(context)

        view.coroutineScope.cancel()
    }

    abstract fun onAuthenticationError()
}

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
internal abstract class StytchScreenView<S : Screen<*>>(context: Context) : ScrollView(context), ScreenView<S> {
    lateinit var coroutineScope: CoroutineScope

    private lateinit var screen: S

    init {
        setBackgroundColor(uiCustomization.backgroundColor.getColor(context))
    }

    override fun setScreen(screen: S) {
        this.screen = screen
    }

    override fun getScreen(): S = screen

    abstract fun subscribeToState()

    protected inline fun <T> StateFlow<T>.subscribe(crossinline collectBlock: suspend (T) -> Unit) {
        coroutineScope.launch(Dispatchers.Main) {
            this@subscribe.collect(collectBlock)
        }
    }

    final override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
    }
}

internal open class StytchEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {
    init {
        setTextColor(uiCustomization.inputTextStyle.color.getColor(context))
        typeface = uiCustomization.inputTextStyle.font.getFont(context)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, uiCustomization.inputTextStyle.size.toFloat())
        // TODO setCursorDrawableColor(style.color.getColor(context))

        setHintTextColor(uiCustomization.inputHintStyle.color.getColor(context))

        background = GradientDrawable().apply {
            cornerRadius = uiCustomization.inputCornerRadius.toFloat()
            setStroke(1.dp.toFloat().toInt(), uiCustomization.inputBackgroundBorderColor.getColor(context))
            val textFieldBackgroundColor = uiCustomization.inputBackgroundColor.getColor(context)
            colors = intArrayOf(textFieldBackgroundColor, textFieldBackgroundColor)
        }
    }

    var isInErrorState = false
        set(value) {
            if (value == field) return
            field = value
            val color = if (field) uiCustomization.errorTextStyle.color else uiCustomization.inputBackgroundBorderColor
            (background as GradientDrawable).setStroke(
                1.dp.toFloat().toInt(),
                color.getColor(context),
            )
        }

    final override fun setTextColor(color: Int) {
        super.setTextColor(color)
    }

    final override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
    }
}

internal open class StytchSingleDigitEditText(context: Context, attrs: AttributeSet?) : StytchEditText(context, attrs) {

}

internal open class StytchTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    textStyle: StytchTextStyle = uiCustomization.subtitleStyle,
) : AppCompatTextView(context, attrs) {
    init {
        setTextColor(textStyle.color.getColor(context))
        typeface = textStyle.font.getFont(context)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, textStyle.size.toFloat())
    }

    final override fun setTextColor(color: Int) {
        super.setTextColor(color)
    }

    final override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
    }
}

internal class StytchTitleTextView(context: Context, attrs: AttributeSet?) : StytchTextView(context, attrs, uiCustomization.titleStyle) {
    init {
        visibility = if (uiCustomization.showTitle) View.VISIBLE else View.GONE
    }
}

internal class StytchSubtitleTextView(context: Context, attrs: AttributeSet?) : StytchTextView(context, attrs, uiCustomization.subtitleStyle) {
    init {
        visibility = if (uiCustomization.showSubtitle) View.VISIBLE else View.GONE
    }
}

internal class StytchSMSConsentTextView(context: Context, attrs: AttributeSet?) : StytchTextView(context, attrs, uiCustomization.consentTextStyle)

internal class StytchErrorTextView(context: Context, attrs: AttributeSet?) : StytchTextView(context, attrs, uiCustomization.errorTextStyle)

internal class StytchButton(context: Context, attrs: AttributeSet?) : AppCompatButton(context, attrs) {
    init {
        val enabledColor = uiCustomization.buttonEnabledBackgroundColor.getColor(context)
        val disabledColor = uiCustomization.buttonDisabledBackgroundColor.getColor(context)

        background = GradientDrawable().apply {
            cornerRadius = uiCustomization.buttonCornerRadius.toFloat()
            colors = intArrayOf(enabledColor, disabledColor)
        }

        val states = arrayOf(intArrayOf(android.R.attr.state_enabled), intArrayOf(-android.R.attr.state_enabled))
        val colors = intArrayOf(enabledColor, disabledColor)
        backgroundTintList = ColorStateList(states, colors)

        transformationMethod = null

        val enabledTextColor = uiCustomization.buttonTextStyle.color.getColor(context)
        val disabledTextColor = uiCustomization.buttonDisabledTextColor.getColor(context)
        val textColors = intArrayOf(enabledTextColor, disabledTextColor)
        setTextColor(ColorStateList(states, textColors))
        typeface = uiCustomization.buttonTextStyle.font.getFont(context)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, uiCustomization.buttonTextStyle.size.toFloat())
    }
}
