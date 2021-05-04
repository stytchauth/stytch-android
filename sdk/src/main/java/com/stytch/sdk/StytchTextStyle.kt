package com.stytch.sdk

import android.graphics.Typeface
import com.stytch.sdk.helpers.dp

public class StytchTextStyle {
    public var size: Float = 10.dp
    public var color: StytchColor = StytchColor.fromColorId(R.color.editTextColor)
    public var font: Typeface? = null
}
