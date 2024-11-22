package com.stytch.exampleapp.b2b

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.ui.b2b.StytchB2BUI
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig

class UIWorkbenchActivity : ComponentActivity() {
    private val stytchUi =
        StytchB2BUI
            .Builder()
            .apply {
                activity(this@UIWorkbenchActivity)
                productConfig(
                    StytchB2BProductConfig(),
                )
                onAuthenticated {
                    when (it) {
                        is StytchResult.Success -> {
                            Toast
                                .makeText(
                                    this@UIWorkbenchActivity,
                                    "Authentication Succeeded",
                                    Toast.LENGTH_LONG,
                                ).show()
                        }
                        is StytchResult.Error -> {
                            Toast.makeText(this@UIWorkbenchActivity, it.exception.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }.build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Button(onClick = stytchUi::authenticate) {
                    Text("Launch Authentication")
                }
            }
        }
    }
}
