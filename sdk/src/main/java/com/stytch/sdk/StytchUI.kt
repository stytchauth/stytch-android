package com.stytch.sdk

import com.stytch.sdk.api.StytchResult
import com.stytch.sdk.helpers.Constants

public class StytchUI private constructor() {

    var uiListener: StytchUIListener? = null

    // TODO ??
    var uiCustomization = StytchUICustomization()
		set(value) {
			try {
				Stytch.instance.config!!.uiCustomization = value
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
