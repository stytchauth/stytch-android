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
    AMAZON(R.drawable.amazon, R.string.stytch_b2c_semantics_amazon_logo, R.string.stytch_b2c_continue_with_amazon),

    /**
     * Support Apple OAuth logins
     */
    APPLE(R.drawable.apple, R.string.stytch_b2c_semantics_apple_logo, R.string.stytch_b2c_continue_with_apple),

    /**
     * Support BitBucket OAuth logins
     */
    BITBUCKET(
        R.drawable.bitbucket,
        R.string.stytch_b2c_semantics_bitbucket_logo,
        R.string.stytch_b2c_continue_with_bitbucket,
    ),

    /**
     * Support Coinbase OAuth logins
     */
    COINBASE(
        R.drawable.coinbase,
        R.string.stytch_b2c_semantics_coinbase_logo,
        R.string.stytch_b2c_continue_with_coinbase,
    ),

    /**
     * Support Discord OAuth logins
     */
    DISCORD(R.drawable.discord, R.string.stytch_b2c_semantics_discord_logo, R.string.stytch_b2c_continue_with_discord),

    /**
     * Support Facebook OAuth logins
     */
    FACEBOOK(
        R.drawable.facebook,
        R.string.stytch_b2c_semantics_facebook_logo,
        R.string.stytch_b2c_continue_with_facebook,
    ),

    /**
     * Support Figma OAuth logins
     */
    FIGMA(R.drawable.figma, R.string.stytch_b2c_semantics_figma_logo, R.string.stytch_b2c_continue_with_figma),

    /**
     * Support GitHub OAuth logins
     */
    GITHUB(R.drawable.github, R.string.stytch_b2c_semantics_github_logo, R.string.stytch_b2c_continue_with_github),

    /**
     * Support GitLab OAuth logins
     */
    GITLAB(R.drawable.gitlab, R.string.stytch_b2c_semantics_gitlab_logo, R.string.stytch_b2c_continue_with_gitlab),

    /**
     * Support Google OAuth logins
     */
    GOOGLE(R.drawable.google, R.string.stytch_b2c_semantics_google_logo, R.string.stytch_b2c_continue_with_google),

    /**
     * Support LinkedIn OAuth logins
     */
    LINKEDIN(
        R.drawable.linkedin,
        R.string.stytch_b2c_semantics_linkedin_logo,
        R.string.stytch_b2c_continue_with_linkedin,
    ),

    /**
     * Support Microsoft OAuth logins
     */
    MICROSOFT(
        R.drawable.microsoft,
        R.string.stytch_b2c_semantics_microsoft_logo,
        R.string.stytch_b2c_continue_with_microsoft,
    ),

    /**
     * Support Salesforce OAuth logins
     */
    SALESFORCE(
        R.drawable.salesforce,
        R.string.stytch_b2c_semantics_salesforce_logo,
        R.string.stytch_b2c_continue_with_salesforce,
    ),

    /**
     * Support Slack OAuth logins
     */
    SLACK(R.drawable.slack, R.string.stytch_b2c_semantics_slack_logo, R.string.stytch_b2c_continue_with_slack),

    /**
     * Support Snapchat OAuth logins
     */
    SNAPCHAT(
        R.drawable.snapchat,
        R.string.stytch_b2c_semantics_snapchat_logo,
        R.string.stytch_b2c_continue_with_snapchat,
    ),

    /**
     * Support TikTok OAuth logins
     */
    TIKTOK(R.drawable.tiktok, R.string.stytch_b2c_semantics_tiktok_logo, R.string.stytch_b2c_continue_with_tiktok),

    /**
     * Support Twitch OAuth logins
     */
    TWITCH(R.drawable.twitch, R.string.stytch_b2c_semantics_twitch_logo, R.string.stytch_b2c_continue_with_twitch),

    /**
     * Support Twitter OAuth logins
     */
    TWITTER(R.drawable.twitter, R.string.stytch_b2c_semantics_twitter_logo, R.string.stytch_b2c_continue_with_twitter),
}
