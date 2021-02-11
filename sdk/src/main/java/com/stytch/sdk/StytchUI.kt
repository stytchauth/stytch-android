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

    var uiCustomization = StytchUICustomization()
		set(value) {
			try {
				Stytch.instance.config!!.uiCustomization = value
			} catch (ex: Exception) {
				throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
			}
        }

        public fun loginViewController(): StytchMainActivity
	{
		return StytchMainActivity()
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