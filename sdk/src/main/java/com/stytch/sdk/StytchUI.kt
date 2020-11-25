package com.stytch.sdk

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stytch.sdk.api.Api
import com.stytch.sdk.api.StytchResult
import com.stytch.sdk.helpers.Constants
import com.stytch.sdk.ui.StytchMainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StytchUI private constructor() {

    internal lateinit var uiListener: StytchUIListener

    fun launchFlow(
        activity: Activity,
        listener: StytchUIListener,
        uiCustomization: StytchCustomization = StytchCustomization()
    ) {
        try {
            uiListener = listener
            Stytch.instance.config?.customization = uiCustomization
            val intent = Intent(activity, StytchMainActivity::class.java)
            activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
        } catch (ex: UninitializedPropertyAccessException) {
            throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
        }
    }

    fun launchFlow(
        activity: AppCompatActivity,
        listener: StytchUIListener,
        uiCustomization: StytchCustomization = StytchCustomization()
    ) {
        try {
            uiListener = listener
            Stytch.instance.config.customization = uiCustomization
            //        "email-test-12fb246b-e78d-4bb9-9e38-f6b7796b4a86"
//        user-test-ff7a8219-70b5-462d-9ec0-ef858fdbdf5f
            val intent = Intent(activity, StytchMainActivity::class.java)
            activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
        } catch (ex: UninitializedPropertyAccessException) {
            throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
        }

    }

    fun launchFlow(
        fragment: Fragment,
        listener: StytchUIListener,
        uiCustomization: StytchCustomization = StytchCustomization()
    ) {
        try {
            uiListener = listener
            Stytch.instance.config?.customization = uiCustomization
            val intent = Intent(fragment.requireContext(), StytchMainActivity::class.java)
            fragment.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
        } catch (ex: UninitializedPropertyAccessException) {
            throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
        }
    }

    interface StytchUIListener {
        fun onEvent() {} //TODO: add event param
        fun onSuccess(result: StytchResult)
        fun onError() //TODO: add error param
    }



    companion object {
        private const val ACTIVITY_REQUEST_CODE = 674

        val instance = StytchUI()
    }
}