package com.stytch.sdk.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.stytch.sdk.R
import com.stytch.sdk.Stytch
import com.stytch.sdk.dp
import com.stytch.sdk.ui.invertedWhiteBlack

internal class StytchWaterMarkView constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var poweredByText: StytchTextView
    lateinit var poweredByImage: AppCompatImageView

    init {
        createTextView()
        createLogo()

        addViews()
    }

    private fun addViews() {
        addView(poweredByText)
        addView(poweredByImage)

        ConstraintSet().apply {

            constrainHeight(poweredByText.id, ConstraintSet.WRAP_CONTENT)
            constrainWidth(poweredByText.id, ConstraintSet.WRAP_CONTENT)

            constrainHeight(poweredByImage.id, 56.dp.toInt())
            constrainWidth(poweredByImage.id, 56.dp.toInt())

            connect(
                poweredByText.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
            )
            connect(
                poweredByText.id,
                ConstraintSet.END,
                poweredByImage.id,
                ConstraintSet.START,
                8.dp.toInt(),
            )
            centerVertically(poweredByText.id, ConstraintSet.PARENT_ID)

            connect(poweredByImage.id, ConstraintSet.START, poweredByText.id, ConstraintSet.END)
            connect(
                poweredByImage.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
            )
            centerVertically(poweredByImage.id, ConstraintSet.PARENT_ID)

            createHorizontalChain(
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT,
                intArrayOf(poweredByText.id, poweredByImage.id),
                null,
                ConstraintSet.CHAIN_PACKED,
            )

            applyTo(this@StytchWaterMarkView)
        }

    }

    private fun createTextView() {
        poweredByText = StytchTextView(context).apply {
            id = View.generateViewId()
            setText(R.string.stytch_watermark_powered_by)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(ContextCompat.getColor(context, R.color.poweredByColor))
        }
    }

    private fun createLogo() {
        Stytch.instance.config?.let { config ->
            val color = config.uiCustomization.backgroundColor.getColor(context).invertedWhiteBlack()

            poweredByImage = AppCompatImageView(context).apply {
                id = View.generateViewId()
                setImageResource(R.drawable.ic_stytch_logo)
            }

            poweredByImage.imageTintList = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(color))
        }
    }

}