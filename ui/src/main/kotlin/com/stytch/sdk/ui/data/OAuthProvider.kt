package com.stytch.sdk.ui.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.oauth.OAuth
import com.stytch.sdk.ui.R

public enum class OAuthProvider(
    @DrawableRes internal val iconDrawable: Int,
    @StringRes internal val iconText: Int,
    @StringRes internal val text: Int,
    internal val handler: OAuth.ThirdParty,
) {
    AMAZON(R.drawable.amazon, R.string.amazon_logo, R.string.continue_with_amazon, StytchClient.oauth.amazon),
    APPLE(R.drawable.apple, R.string.apple_logo, R.string.continue_with_apple, StytchClient.oauth.apple),
    BITBUCKET(
        R.drawable.bitbucket,
        R.string.bitbucket_logo,
        R.string.continue_with_bitbucket,
        StytchClient.oauth.bitbucket
    ),
    COINBASE(R.drawable.coinbase, R.string.coinbase_logo, R.string.continue_with_coinbase, StytchClient.oauth.coinbase),
    DISCORD(R.drawable.discord, R.string.discord_logo, R.string.continue_with_discord, StytchClient.oauth.discord),
    FACEBOOK(R.drawable.facebook, R.string.facebook_logo, R.string.continue_with_facebook, StytchClient.oauth.facebook),
    FIGMA(R.drawable.figma, R.string.figma_logo, R.string.continue_with_figma, StytchClient.oauth.figma),
    GITHUB(R.drawable.github, R.string.github_logo, R.string.continue_with_github, StytchClient.oauth.github),
    GITLAB(R.drawable.gitlab, R.string.gitlab_logo, R.string.continue_with_gitlab, StytchClient.oauth.gitlab),
    GOOGLE(R.drawable.google, R.string.google_logo, R.string.continue_with_google, StytchClient.oauth.google),
    LINKEDIN(R.drawable.linkedin, R.string.linkedin_logo, R.string.continue_with_linkedin, StytchClient.oauth.linkedin),
    MICROSOFT(
        R.drawable.microsoft,
        R.string.microsoft_logo,
        R.string.continue_with_microsoft,
        StytchClient.oauth.microsoft
    ),
    SALESFORCE(
        R.drawable.salesforce,
        R.string.salesforce_logo,
        R.string.continue_with_salesforce,
        StytchClient.oauth.salesforce
    ),
    SLACK(R.drawable.slack, R.string.slack_logo, R.string.continue_with_slack, StytchClient.oauth.slack),
    SNAPCHAT(R.drawable.snapchat, R.string.snapchat_logo, R.string.continue_with_snapchat, StytchClient.oauth.snapchat),
    TIKTOK(R.drawable.tiktok, R.string.tiktok_logo, R.string.continue_with_tiktok, StytchClient.oauth.tiktok),
    TWITCH(R.drawable.twitch, R.string.twitch_logo, R.string.continue_with_twitch, StytchClient.oauth.twitch),
    TWITTER(R.drawable.twitter, R.string.twitter_logo, R.string.continue_with_twitter, StytchClient.oauth.twitter),
}
