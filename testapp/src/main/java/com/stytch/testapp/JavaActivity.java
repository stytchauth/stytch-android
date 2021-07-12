package com.stytch.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.stytch.sdk.Stytch;
import com.stytch.sdk.StytchActivityLauncher;
import com.stytch.sdk.StytchCallbackApi;
import com.stytch.sdk.StytchEnvironment;
import com.stytch.sdk.StytchUI;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import timber.log.Timber;

public class JavaActivity extends AppCompatActivity {
    TextView resultTextView;

    StytchActivityLauncher stytchEmailMagicLinkActivityLauncher =
            StytchUI.EmailMagicLink.activityLauncher(this, result -> {
                showResult(result);
                return Unit.INSTANCE;
            });

    StytchActivityLauncher stytchSmsPasscodeActivityLauncher =
            StytchUI.SMSPasscode.activityLauncher(this, result -> {
                showResult(result);
                return Unit.INSTANCE;
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stytch.configure(
                "public-token-test-792e8013-4a7c-4d7c-848f-9fc94fc8ba73",
                StytchEnvironment.TEST
        );

        Button toggleActivityButton = findViewById(R.id.toggle_activity_button);
        toggleActivityButton.setText("Switch to Kotlin Activity");
        toggleActivityButton.setOnClickListener(view -> switchToKotlinActivity());
        findViewById(R.id.toggle_dark_mode_button).setOnClickListener(view -> toggleDarkMode());
        findViewById(R.id.magic_link_direct_api_button).setOnClickListener(view -> testMagicLinkDirectApi());
        findViewById(R.id.magic_link_ui_flow_button).setOnClickListener(view -> testMagicLinkUIFlow());
        findViewById(R.id.sms_passcode_ui_flow_button).setOnClickListener(view -> testSmsPasscodeUIFlow());
        resultTextView = findViewById(R.id.result_text_view);
    }

    private void switchToKotlinActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void toggleDarkMode() {
        if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void showResult(Object result) {
        String asString = result.toString();
        new Handler(getMainLooper()).post(() -> {
            resultTextView.setText(asString);
            UtilKt.showToast(this, asString);
            Timber.tag("StytchTestApp").i("Result received: %s", asString);
        });
    }

    private void testMagicLinkDirectApi() {
        StytchCallbackApi.MagicLinks.Email.loginOrCreateUser(
                "kyle@stytch.com",
                "https://test.stytch.com/login",
                "https://test.stytch.com/signup",
                null,
                null,
                null,
                null,
                result -> {
                    showResult(result);
                    return Unit.INSTANCE;
                }
        );
    }

    private void testMagicLinkUIFlow() {
        StytchUI.EmailMagicLink.configure(
                "https://test.stytch.com/login",
                "https://test.stytch.com/signup",
                true,
                new StytchUI.EmailMagicLink.Authenticator() {
                    @Override
                    public void authenticateToken(@NotNull String token) {
                        boolean success = true;
                        onComplete(success);
                    }
                }
        );
        stytchEmailMagicLinkActivityLauncher.launch();
    }

    private void testSmsPasscodeUIFlow() {
        StytchUI.SMSPasscode.configure(
                false,
                new StytchUI.SMSPasscode.Authenticator() {
                    @Override
                    public void authenticateToken(@NotNull String methodId, @NotNull String token) {
                        boolean success = true;
                        onComplete(success);
                    }
                }
        );
        stytchSmsPasscodeActivityLauncher.launch();
    }
}
