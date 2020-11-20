package com.stytch.sdk.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.stytch.sdk.R
import com.stytch.sdk.helpers.dp

class StytchEditText(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int)
    : AppCompatEditText(context, attributeSet, defStyleAttr){

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, android.R.attr.editTextStyle)

    init{
        setBackgroundResource(R.drawable.stytch_edit_bg)
        setPadding(16.dp, 16.dp, 16.dp, 16.dp)
    }

}