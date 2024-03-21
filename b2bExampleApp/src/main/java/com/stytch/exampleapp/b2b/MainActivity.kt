package com.stytch.exampleapp.b2b

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.stytch.exampleapp.b2b.theme.AppTheme
import com.stytch.exampleapp.b2b.ui.AppScreen

internal const val SSO_REQUEST_ID = 2

class MainActivity : FragmentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val passwordsViewModel: PasswordsViewModel by viewModels()
    private val discoveryViewModel: DiscoveryViewModel by viewModels()
    private val ssoViewModel: SSOViewModel by viewModels()
    private val memberViewModel: MemberViewModel by viewModels()
    private val organizationViewModel: OrganizationViewModel by viewModels()
    private val otpViewModel: OTPViewModel by viewModels()
    private val totpViewModel: TOTPViewModel by viewModels()
    private val recoveryCodesViewModel: RecoveryCodesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppScreen(
                    homeViewModel,
                    passwordsViewModel,
                    discoveryViewModel,
                    ssoViewModel,
                    memberViewModel,
                    organizationViewModel,
                    otpViewModel,
                    totpViewModel,
                    recoveryCodesViewModel,
                )
            }
        }
        if (intent.action == Intent.ACTION_VIEW) {
            handleIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent) {
        intent.data?.let { appLinkData ->
            Toast.makeText(this, getString(R.string.deeplink_received_toast), Toast.LENGTH_LONG).show()
            homeViewModel.handleUri(appLinkData)
        }
    }

    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SSO_REQUEST_ID -> {
                ssoViewModel.authenticateSSO(resultCode = resultCode, intent = data)
            }
        }
    }
}
