package com.stytch.sdk.ui.b2c.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.stytch.sdk.R

/**
 * An enum class representing all of the supported OAuth providers
 */

public enum class OAuthProvider(
    @DrawableRes internal val iconDrawable: Int,
    @StringRes internal val providerName: Int,
) {
    /**
     * Support Amazon OAuth logins
     */
    AMAZON(R.drawable.amazon, R.string.stytch_provider_name_amazon),

    /**
     * Support Apple OAuth logins
     */
    APPLE(R.drawable.apple, R.string.stytch_provider_name_apple),

    /**
     * Support BitBucket OAuth logins
     */
    BITBUCKET(R.drawable.bitbucket, R.string.stytch_provider_name_bitbucket),

    /**
     * Support Coinbase OAuth logins
     */
    COINBASE(R.drawable.coinbase, R.string.stytch_provider_name_coinbase),

    /**
     * Support Discord OAuth logins
     */
    DISCORD(R.drawable.discord, R.string.stytch_provider_name_discord),

    /**
     * Support Facebook OAuth logins
     */
    FACEBOOK(R.drawable.facebook, R.string.stytch_provider_name_facebook),

    /**
     * Support Figma OAuth logins
     */
    FIGMA(R.drawable.figma, R.string.stytch_provider_name_figma),

    /**
     * Support GitHub OAuth logins
     */
    GITHUB(R.drawable.github, R.string.stytch_provider_name_github),

    /**
     * Support GitLab OAuth logins
     */
    GITLAB(R.drawable.gitlab, R.string.stytch_provider_name_gitlab),

    /**
     * Support Google OAuth logins
     */
    GOOGLE(R.drawable.google, R.string.stytch_provider_name_google),

    /**
     * Support LinkedIn OAuth logins
     */
    LINKEDIN(R.drawable.linkedin, R.string.stytch_provider_name_linkedin),

    /**
     * Support Microsoft OAuth logins
     */
    MICROSOFT(R.drawable.microsoft, R.string.stytch_provider_name_microsoft),

    /**
     * Support Salesforce OAuth logins
     */
    SALESFORCE(R.drawable.salesforce, R.string.stytch_provider_name_salesforce),

    /**
     * Support Slack OAuth logins
     */
    SLACK(R.drawable.slack, R.string.stytch_provider_name_slack),

    /**
     * Support Snapchat OAuth logins
     */
    SNAPCHAT(R.drawable.snapchat, R.string.stytch_provider_name_snapchat),

    /**
     * Support TikTok OAuth logins
     */
    TIKTOK(R.drawable.tiktok, R.string.stytch_provider_name_tiktok),

    /**
     * Support Twitch OAuth logins
     */
    TWITCH(R.drawable.twitch, R.string.stytch_provider_name_twitch),

    /**
     * Support Twitter OAuth logins
     */
    TWITTER(R.drawable.twitter, R.string.stytch_provider_name_twitter),
}
