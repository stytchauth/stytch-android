package com.stytch.sdk.ui.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.stytch.sdk.ui.R

public enum class OAuthProvider(
    @DrawableRes internal val iconDrawable: Int,
    @StringRes internal val iconText: Int,
    @StringRes internal val text: Int,
) {
    // AMAZON("amazon"),
    APPLE(R.drawable.apple, R.string.apple_logo, R.string.continue_with_apple),

    // BITBUCKET("bitbucket"),
    // COINBASE("coinbase"),
    // DISCORD("discord"),
    // FACEBOOK("facebook"),
    // FIGMA("figma"),
    // GITHUB("github"),
    // GITLAB("gitlab"),
    GOOGLE(R.drawable.google, R.string.google_logo, R.string.continue_with_google),
    // LINKEDIN("linkedin"),
    // MICROSOFT("microsoft"),
    // SALESFORCE("salesforce"),
    // SLACK("slack"),
    // SNAPCHAT("snapchat"),
    // TIKTOK("tiktok"),
    // TWITCH("twitch"),
    // TWITTER("twitter"),
}
