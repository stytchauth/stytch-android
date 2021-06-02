package com.stytch.sdk

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.wealthfront.magellan.Navigator

public abstract class StytchActivity internal constructor() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityId = intent.getIntExtra(Constants.ACTIVITY_ID_EXTRA_NAME, -1)
        if (navigator == null || activityId != currentActivityId) {
            currentActivityId = activityId
            navigator = createNavigator()
        }
        setContentView(R.layout.stytch_activity_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        actionBar?.customize()
        supportActionBar?.customize()
    }

    protected abstract fun createNavigator(): Navigator

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
        navigator?.onCreate(this, savedInstanceState)
        super.onPostCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigator?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        navigator?.onResume(this)
    }

    override fun onPause() {
        navigator?.onPause(this)
        super.onPause()
    }

    override fun onDestroy() {
        navigator?.onDestroy(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (navigator?.handleBack() != true) {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        navigator?.onCreateOptionsMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        navigator?.onPrepareOptionsMenu(menu)
        return super.onPrepareOptionsMenu(menu)
    }

    internal companion object {
        @SuppressLint("StaticFieldLeak")
        var navigator: Navigator? = null
        private var currentActivityId = -1

        private var nextActivityId = 0

        fun getNextActivityId(): Int {
            val returnVal = nextActivityId
            nextActivityId++
            return returnVal
        }
    }
}
