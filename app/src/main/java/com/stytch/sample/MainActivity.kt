package com.stytch.sample

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.stytch.sdk.*
import com.stytch.sdk.api.Api
import com.stytch.sdk.api.StytchResult
import com.stytch.sdk.helpers.LoggerLocal
import com.stytch.sdk.helpers.dp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun launchStytch(view: View) {

        val secret = "secret-test-6-ma0PNENqjBVX6Dx2aPUIdhLFObauXx07c="
        val projectId = "project-test-d0dbafe6-a019-47ea-8550-d021c1c76ea9"
        Stytch.instance.configure(
            projectId,
            secret,
            "https",
            true
        )

//        Stytch.instance.login("demid@stytch.com")



        StytchUI.instance.launchFlow(
            this,
            object: StytchUI.StytchUIListener{
                override fun onSuccess(result: StytchResult) {
                    LoggerLocal.d(TAG,"onSuccess: $result")
//                    TODO("Not yet implemented")
                }

                override fun onError() {
//                    TODO("Not yet implemented")
                }

            },
            createCustomization()
        )
    }

    private fun createCustomization(): StytchCustomization {
        return StytchCustomization().apply {
            this.backgroundId = R.color.purple_200
            buttonBackgroundColorId = R.color.purple_500
            editBackgroundColorId = android.R.color.holo_orange_light
            showBrandLogo = false

            titleCustomization = StytchTextCustomization().apply {
                size = 10.dp
                colorId = android.R.color.holo_red_dark
                font = Typeface.create(null as Typeface?, Typeface.BOLD)
            }

            subtitleCustomization.apply {
                size = 23.dp
                colorId = android.R.color.holo_blue_bright
                font = Typeface.create(null as Typeface?, Typeface.ITALIC)
            }

            editHintCustomization.apply {
                size = 12.dp
                colorId = android.R.color.holo_green_dark
                font = Typeface.create(null as Typeface?, Typeface.BOLD)
            }

            editTextCustomization.apply {
                colorId = android.R.color.white
                font = Typeface.create(null as Typeface?, Typeface.ITALIC)
            }

            buttonTextCustomization.apply {
                colorId = android.R.color.holo_blue_dark
                font = Typeface.create(null as Typeface?, Typeface.ITALIC)
                size = 4.dp
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}