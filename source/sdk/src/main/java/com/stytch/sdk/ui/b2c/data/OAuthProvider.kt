package com.stytch.sdk.ui.b2c.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.stytch.sdk.R

/**
 * An enum class representing all of the supported OAuth providers
 */

public enum class OAuthProvider(
    @DrawableRes internal val iconDrawable: Int,
    @StringRes internal val iconText: Int,
    @StringRes internal val text: Int,
) {
    /**
     * Support Amazon OAuth logins
     */
    AMAZON(R.drawable.amazon, R.string.amazon_logo, R.string.continue_with_amazon),

    /**
     * Support Apple OAuth logins
     */
    APPLE(R.drawable.apple, R.string.apple_logo, R.string.continue_with_apple),

    /**
     * Support BitBucket OAuth logins
     */
    BITBUCKET(R.drawable.bitbucket, R.string.bitbucket_logo, R.string.continue_with_bitbucket),

    /**
     * Support Coinbase OAuth logins
     */
    COINBASE(R.drawable.coinbase, R.string.coinbase_logo, R.string.continue_with_coinbase),

    /**
     * Support Discord OAuth logins
     */
    DISCORD(R.drawable.discord, R.string.discord_logo, R.string.continue_with_discord),

    /**
     * Support Facebook OAuth logins
     */
    FACEBOOK(R.drawable.facebook, R.string.facebook_logo, R.string.continue_with_facebook),

    /**
     * Support Figma OAuth logins
     */
    FIGMA(R.drawable.figma, R.string.figma_logo, R.string.continue_with_figma),

    /**
     * Support GitHub OAuth logins
     */
    GITHUB(R.drawable.github, R.string.github_logo, R.string.continue_with_github),

    /**
     * Support GitLab OAuth logins
     */
    GITLAB(R.drawable.gitlab, R.string.gitlab_logo, R.string.continue_with_gitlab),

    /**
     * Support Google OAuth logins
     */
    GOOGLE(R.drawable.google, R.string.google_logo, R.string.continue_with_google),

    /**
     * Support LinkedIn OAuth logins
     */
    LINKEDIN(R.drawable.linkedin, R.string.linkedin_logo, R.string.continue_with_linkedin),

    /**
     * Support Microsoft OAuth logins
     */
    MICROSOFT(R.drawable.microsoft, R.string.microsoft_logo, R.string.continue_with_microsoft),

    /**
     * Support Salesforce OAuth logins
     */
    SALESFORCE(R.drawable.salesforce, R.string.salesforce_logo, R.string.continue_with_salesforce),

    /**
     * Support Slack OAuth logins
     */
    SLACK(R.drawable.slack, R.string.slack_logo, R.string.continue_with_slack),

    /**
     * Support Snapchat OAuth logins
     */
    SNAPCHAT(R.drawable.snapchat, R.string.snapchat_logo, R.string.continue_with_snapchat),

    /**
     * Support TikTok OAuth logins
     */
    TIKTOK(R.drawable.tiktok, R.string.tiktok_logo, R.string.continue_with_tiktok),

    /**
     * Support Twitch OAuth logins
     */
    TWITCH(R.drawable.twitch, R.string.twitch_logo, R.string.continue_with_twitch),

    /**
     * Support Twitter OAuth logins
     */
    TWITTER(R.drawable.twitter, R.string.twitter_logo, R.string.continue_with_twitter),
}
