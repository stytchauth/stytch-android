package com.stytch.sdk.helpers

import android.content.res.Resources

val Number.px: Int
    get() = (this.toFloat() / Resources.getSystem().displayMetrics.density).toInt()

val Number.dp: Int
    get() = (this.toFloat() * Resources.getSystem().displayMetrics.density).toInt()
