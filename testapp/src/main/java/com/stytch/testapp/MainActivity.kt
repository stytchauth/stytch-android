package com.stytch.testapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchColor
import com.stytch.sdk.StytchEnvironment
import com.stytch.sdk.StytchResult
import com.stytch.sdk.StytchUI
import com.stytch.sdk.StytchUICustomization
import com.stytch.sdk.dp
import com.stytch.sdk.ui.StytchMainActivity

class MainActivity : AppCompatActivity(), StytchUI.StytchUIListener {
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Stytch.instance.configure(
            projectID = "project-test-2328d332-7850-486a-99c6-cd7956b518c2",
            secret = "secret-test-2tbeMPP3cYW-jXbpt-Ag1yeYDUW4ReEBxHw=",
            scheme = "https",
            host = "test.stytch.com",
        )
        Stytch.instance.environment = StytchEnvironment.TEST
        StytchUI.instance.uiListener = this
        StytchUI.instance.uiCustomization = StytchUICustomization().apply {
            buttonCornerRadius = 7.dp
            buttonBackgroundColor = StytchColor.fromColorId(R.color.purple_200)
            inputBackgroundColor = StytchColor.fromColorId(R.color.design_default_color_background)
            inputBackgroundBorderColor = StytchColor.fromColorId(R.color.design_default_color_secondary_variant)
            inputCornerRadius = 3.dp
            backgroundColor = StytchColor.fromColorId(R.color.design_default_color_background)
            showBrandLogo = false
            showTitle = false
            showSubtitle = false
        }

        loginButton = findViewById(R.id.launch_stytch_ui_button)

        loginButton.setOnClickListener {
            val launchStytchIntent = Intent(this, StytchMainActivity::class.java)
            startActivity(launchStytchIntent)
        }
    }

    override fun onSuccess(result: StytchResult) {
        Toast.makeText(this, "SUCCESS!!!", Toast.LENGTH_LONG).show()
    }

    override fun onFailure() {
        Toast.makeText(this, "FAILURE :(", Toast.LENGTH_LONG).show()
    }
}
