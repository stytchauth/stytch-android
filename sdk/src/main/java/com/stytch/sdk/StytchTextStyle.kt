package com.stytch.sdk

import android.graphics.Typeface
import com.stytch.sdk.helpers.dp

public class StytchTextStyle {
    var size: Float = 10.dp
    var color: StytchColor = StytchColor.fromColorId(R.color.editTextColor)
    var font: Typeface? = null
}
