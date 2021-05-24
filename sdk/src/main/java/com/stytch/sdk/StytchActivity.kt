package com.stytch.sdk

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.wealthfront.magellan.Navigator
import com.wealthfront.magellan.Screen

public abstract class StytchActivity internal constructor(private val startScreen: Screen<*>) : AppCompatActivity() {
    protected lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stytch_activity_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        actionBar?.customize()
        supportActionBar?.customize()
        navigator = Navigator.withRoot(startScreen).build()
    }

    private fun android.app.ActionBar.customize() {
        with(StytchUI.uiCustomization) {
            if (hideActionBar) hide()
            setBackgroundDrawable(
                GradientDrawable().apply {
                    setColor(actionBarColor.getColor(this@StytchActivity))
                }
            )
        }
    }

    private fun ActionBar.customize() {
        with(StytchUI.uiCustomization) {
            if (hideActionBar) hide()
            setBackgroundDrawable(
                GradientDrawable().apply {
                    setColor(actionBarColor.getColor(this@StytchActivity))
                }
            )
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        navigator.onCreate(this, savedInstanceState)
        super.onPostCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigator.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        navigator.onResume(this)
    }

    override fun onPause() {
        navigator.onPause(this)
        super.onPause()
    }

    override fun onDestroy() {
        navigator.onDestroy(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!navigator.handleBack()) {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        navigator.onCreateOptionsMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        navigator.onPrepareOptionsMenu(menu)
        return super.onPrepareOptionsMenu(menu)
    }
}
