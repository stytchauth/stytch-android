package com.stytch.sdk.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stytch.sdk.R
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchUI


internal class StytchMainActivity : AppCompatActivity() {

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
            loginFragment.onNewIntent(data)
        }
        super.onNewIntent(intent)
    }


    companion object {
        private const val TAG = "StytchMainActivity"
        private const val LOGIN_FRAGMENT_TAG = "loginFragment"
    }

}