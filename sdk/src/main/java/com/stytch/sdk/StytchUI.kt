package com.stytch.sdk

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stytch.sdk.api.StytchResult
import com.stytch.sdk.helpers.Constants
import com.stytch.sdk.ui.StytchMainActivity

public class StytchUI private constructor() {

    internal var uiListener: StytchUIListener? = null

    public fun showUI(
        activity: Activity,
        listener: StytchUIListener,
        uiCustomization: StytchUICustomization = StytchUICustomization()
    ) {
        try {
            uiListener = listener
            Stytch.instance.config!!.uiCustomization = uiCustomization
            val intent = Intent(activity, StytchMainActivity::class.java)
            activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
        } catch (ex: Exception) {
            throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
        }
    }

    public fun showUI(
        activity: AppCompatActivity,
        listener: StytchUIListener,
        uiUICustomization: StytchUICustomization = StytchUICustomization()
    ) {
        try {
            uiListener = listener
            Stytch.instance.config!!.uiCustomization = uiUICustomization
            val intent = Intent(activity, StytchMainActivity::class.java)
            activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
        } catch (ex: Exception) {
            throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
        }
    }

    public fun showUI(
        fragment: Fragment,
        listener: StytchUIListener,
        uiUICustomization: StytchUICustomization = StytchUICustomization()
    ) {
        try {
            uiListener = listener
            Stytch.instance.config!!.uiCustomization = uiUICustomization
            val intent = Intent(fragment.requireContext(), StytchMainActivity::class.java)
            fragment.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
        } catch (ex: Exception) {
            throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
        }
    }

    public interface StytchUIListener {
        fun onEvent(event: StytchEvent) {}
        fun onSuccess(result: StytchResult)
        fun onFailure()
    }

    public companion object {
        private const val ACTIVITY_REQUEST_CODE = 674

        public val instance = StytchUI()
    }
}