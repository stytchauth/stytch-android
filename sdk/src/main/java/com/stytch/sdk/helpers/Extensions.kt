package com.stytch.sdk.helpers

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.stytch.sdk.Stytch

val Number.px: Float
    get() = this.toFloat() / Resources.getSystem().displayMetrics.density

val Number.dp: Float
    get() = this.toFloat() * Resources.getSystem().displayMetrics.density

internal fun Fragment.hideKeyboard() {
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
}

internal fun Int.invertedWhiteBlack(): Int {
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    return if ((red * 0.299 + green * 0.587 + blue * 0.114) > 140) Color.BLACK else Color.WHITE
}

internal fun String.deepLink(): String{
    return "${Stytch.instance.config.deepLinkScheme}://${Stytch.instance.config.deepLinkHost}/$this"
}
