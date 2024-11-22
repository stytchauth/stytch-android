package com.stytch.javademoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.stytch.sdk.common.StytchResult;
import com.stytch.sdk.consumer.StytchClient;
import com.stytch.sdk.ui.b2c.StytchUI;
import com.stytch.sdk.ui.b2c.data.EmailMagicLinksOptions;
import com.stytch.sdk.ui.b2c.data.OAuthOptions;
import com.stytch.sdk.ui.b2c.data.OAuthProvider;
import com.stytch.sdk.ui.b2c.data.OTPMethods;
import com.stytch.sdk.ui.b2c.data.OTPOptions;
import com.stytch.sdk.ui.b2c.data.StytchProduct;
import com.stytch.sdk.ui.b2c.data.StytchProductConfig;

import java.util.List;

import kotlin.Unit;

public class MainActivity extends AppCompatActivity {
    private Button loginButton;
    private Button logoutButton;
    private TextView loading;
    private final AppCompatActivity self = this;
    private final StytchUI stytchUI = new StytchUI.Builder()
        .activity(self)
        .productConfig(new StytchProductConfig(
                List.of(StytchProduct.OAUTH,
                        StytchProduct.EMAIL_MAGIC_LINKS,
                        StytchProduct.OTP,
                        StytchProduct.PASSWORDS
                ),
                new EmailMagicLinksOptions(),
                new OAuthOptions(
                        "stytchjavademoapp://oauth?type=login",
                        "stytchjavademoapp://oauth?type=signup",
                        List.of(OAuthProvider.GOOGLE, OAuthProvider.APPLE, OAuthProvider.GITHUB)
                ),
                new OTPOptions(
                        List.of(OTPMethods.SMS, OTPMethods.WHATSAPP)
                )
        ))
        .onAuthenticated(this::onAuthenticated)
        .build();
    private final Observer<StytchState> stytchStateObserver = this::handleStytchStateChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loading = findViewById(R.id.loadingText);
        loginButton = findViewById(R.id.loginButton);
        logoutButton = findViewById(R.id.logoutButton);
        setDefaultUIState();

        viewModel.getStytchState().observe(this, stytchStateObserver);

        StytchClient.configure(
            this.getApplicationContext(),
            BuildConfig.STYTCH_PUBLIC_TOKEN,
            viewModel::handleInitializationChange
        );

        loginButton.setOnClickListener(v -> stytchUI.authenticate());
        logoutButton.setOnClickListener(v -> viewModel.logout());
    }

    private void handleStytchStateChange(StytchState stytchState) {
        if (stytchState.isInitialized()) {
            loading.setVisibility(View.INVISIBLE);
            if (stytchState.userData() != null && stytchState.sessionData() != null) {
                loginButton.setVisibility(View.INVISIBLE);
                logoutButton.setVisibility(View.VISIBLE);
            } else {
                loginButton.setVisibility(View.VISIBLE);
                logoutButton.setVisibility(View.INVISIBLE);
            }
        } else {
            setDefaultUIState();
        }
    }

    private void setDefaultUIState() {
        loading.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        logoutButton.setVisibility(View.INVISIBLE);
    }

    private Unit onAuthenticated(StytchResult<?> result) {
        return Unit.INSTANCE;
    }
}