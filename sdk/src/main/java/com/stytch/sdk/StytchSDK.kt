package com.stytch.sdk

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stytch.sdk.api.Api
import com.stytch.sdk.ui.StytchMainActivity

class StytchSDK() {

    val config: StytchConfig = StytchConfig()

    fun launchFlow(activity: Activity) {
        val intent = Intent(activity, StytchMainActivity::class.java)
        activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
    }

    fun launchFlow(activity: AppCompatActivity) {
        val intent = Intent(activity, StytchMainActivity::class.java)
        activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
    }

    fun launchFlow(fragment: Fragment) {
        val intent = Intent(fragment.requireContext(), StytchMainActivity::class.java)
        fragment.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
    }



    companion object {
        private const val ACTIVITY_REQUEST_CODE = 1

        val instance = StytchSDK()
    }
}