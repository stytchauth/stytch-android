package com.stytch.sdk

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stytch.sdk.api.StytchResult
import com.stytch.sdk.helpers.Constants
import com.stytch.sdk.ui.StytchMainActivity

class StytchUI private constructor() {

    internal var uiListener: StytchUIListener? = null

    fun showUI(
        activity: Activity,
        listener: StytchUIListener,
        uiCustomization: StytchUICustomization = StytchUICustomization()
    ) {
        try {
            uiListener = listener
            Stytch.instance.config?.uiCustomization = uiCustomization
            val intent = Intent(activity, StytchMainActivity::class.java)
            activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
        } catch (ex: UninitializedPropertyAccessException) {
            throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
        }
    }

    fun showUI(
        activity: AppCompatActivity,
        listener: StytchUIListener,
        uiUICustomization: StytchUICustomization = StytchUICustomization()
    ) {
        try {
            uiListener = listener
            Stytch.instance.config.uiCustomization = uiUICustomization
            //        "email-test-12fb246b-e78d-4bb9-9e38-f6b7796b4a86"
//        user-test-ff7a8219-70b5-462d-9ec0-ef858fdbdf5f
            val intent = Intent(activity, StytchMainActivity::class.java)
            activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
        } catch (ex: UninitializedPropertyAccessException) {
            throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
        }

    }

    fun showUI(
        fragment: Fragment,
        listener: StytchUIListener,
        uiUICustomization: StytchUICustomization = StytchUICustomization()
    ) {
        try {
            uiListener = listener
            Stytch.instance.config?.uiCustomization = uiUICustomization
            val intent = Intent(fragment.requireContext(), StytchMainActivity::class.java)
            fragment.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
        } catch (ex: UninitializedPropertyAccessException) {
            throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
        }
    }

    interface StytchUIListener {
        fun onEvent(event: StytchEvent) {}
        fun onSuccess(result: StytchResult)
        fun onFailure()
    }

    companion object {
        private const val ACTIVITY_REQUEST_CODE = 674

        val instance = StytchUI()
    }
}