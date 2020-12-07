package com.stytch.sample

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.stytch.sdk.*
import com.stytch.sdk.api.StytchResult

internal class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun launchStytch(view: View) {

        val secret = "secret-test-6-ma0PNENqjBVX6Dx2aPUIdhLFObauXx07c="
        val projectId = "project-test-d0dbafe6-a019-47ea-8550-d021c1c76ea9"

        Stytch.instance.configure (
            projectId,
            secret,
            "https",
            "stytch.com"
        )

        Stytch.instance.environment = StytchEnvironment.TEST
        Stytch.instance.loginMethod = StytchLoginMethod.LoginOrInvite

        StytchUI.instance.showUI(
            this,
            object : StytchUI.StytchUIListener {

                override fun onSuccess(result: StytchResult) {
                    Log.d(TAG, "onSuccess: $result")
                }

                override fun onFailure() {
                }

            },
            createCustomization()
        )
    }

    private fun createCustomization(): StytchUICustomization {
        return StytchUICustomization().apply {
//            this.backgroundId = R.color.purple_200
//            buttonBackgroundColorId = R.color.purple_500
//            inputBackgroundColorId = android.R.color.holo_orange_light
//            showBrandLogo = false
//            showSubtitle = false
//            showTitle = false
//
//            titleStyle = StytchTextStyle().apply {
//                size = 10.dp
//                colorId = android.R.color.holo_red_dark
//                font = Typeface.create(null as Typeface?, Typeface.BOLD)
//            }
//
//            subtitleStyle.apply {
//                size = 23.dp
//                colorId = android.R.color.holo_blue_bright
//                font = Typeface.create(null as Typeface?, Typeface.ITALIC)
//            }
//
//            editHintStyle.apply {
//                size = 12.dp
//                colorId = android.R.color.holo_green_dark
//                font = Typeface.create(null as Typeface?, Typeface.BOLD)
//            }
//
//            editTextStyle.apply {
//                colorId = android.R.color.white
//                font = Typeface.create(null as Typeface?, Typeface.ITALIC)
//            }
//
//            buttonTextStyle.apply {
//                colorId = android.R.color.holo_blue_dark
//                font = Typeface.create(null as Typeface?, Typeface.ITALIC)
//                size = 4.dp
//            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}