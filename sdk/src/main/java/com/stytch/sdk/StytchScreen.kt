@file:Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")

package com.stytch.sdk

import android.content.Context
import androidx.annotation.CallSuper
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal abstract class StytchScreen<V : StytchScreenView<*>> : Screen<V>() {
    @CallSuper
    override fun onShow(context: Context?) {
        super.onShow(context)

        view.coroutineScope = StytchScreenViewCoroutineScope()
        view.subscribeToState()
    }

    @CallSuper
    override fun onHide(context: Context?) {
        super.onHide(context)

        view.coroutineScope.cancel()
    }
}

internal abstract class StytchScreenView<S : Screen<*>>(context: Context) : BaseScreenView<S>(context) {
    lateinit var coroutineScope: CoroutineScope

    abstract fun subscribeToState()

    protected inline fun <T> StateFlow<T>.subscribe(crossinline collectBlock: suspend (T) -> Unit) {
        coroutineScope.launch(Dispatchers.Main) {
            this@subscribe.collect(collectBlock)
        }
    }
}
