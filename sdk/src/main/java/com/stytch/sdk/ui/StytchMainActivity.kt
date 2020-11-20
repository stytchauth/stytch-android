package com.stytch.sdk.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.stytch.sdk.R
import com.stytch.sdk.StytchSDK
import com.stytch.sdk.helpers.LoggerLocal


class StytchMainActivity : AppCompatActivity() {

    private var loginFragment: StytchLoginFragment = StytchLoginFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stytch_main)
        showFragment()
    }

    private fun showFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentHolder, loginFragment, LOGIN_FRAGMENT_TAG)
            commit()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        val action: String? = intent?.action
        val data = intent?.data ?: return


        if (action == Intent.ACTION_VIEW) {
            if (data.host == StytchSDK.instance.config.deepLinkHost
                && data.scheme == StytchSDK.instance.config.deepLinkScheme
            ) {
                loginFragment.verifyToken(data.getQueryParameter("token"))
            }
        }
        super.onNewIntent(intent)
    }


    companion object {
        private const val TAG = "StytchMainActivity"
        private const val LOGIN_FRAGMENT_TAG = "loginFragment"
    }

}