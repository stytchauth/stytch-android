package com.stytch.sdk.consumer.magicLinks

import android.os.Parcelable
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.consumer.AuthResponse
import kotlinx.parcelize.Parcelize
import java.util.concurrent.CompletableFuture

/**
 * MagicLinks interface that encompasses authentication functions as well as other related functionality
 */

public interface MagicLinks {
    /**
     * Data class used for wrapping parameters used with MagicLinks authentication
     * @property token is the unique sequence of characters used to log in
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    @JacocoExcludeGenerated
    public data class AuthParameters
        @JvmOverloads
        constructor(
            val token: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
        )

    /**
     * Public variable that exposes an instance of EmailMagicLinks
     */
    public val email: EmailMagicLinks

    /**
     * Authenticate a user given a magic link. This endpoint verifies that the magic link token is valid, hasn't expired
     * or been previously used.
     * @param parameters required to authenticate
     * @return [AuthResponse]
     */
    public suspend fun authenticate(parameters: AuthParameters): AuthResponse

    /**
     * Authenticate a user given a magic link. This endpoint verifies that the magic link token is valid, hasn't expired
     * or been previously used.
     * @param parameters required to authenticate
     * @param callback a callback that receives an [AuthResponse]
     */
    public fun authenticate(
        parameters: AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    )

    /**
     * Authenticate a user given a magic link. This endpoint verifies that the magic link token is valid, hasn't expired
     * or been previously used.
     * @param parameters required to authenticate
     * @return [AuthResponse]
     */
    public fun authenticateCompletable(parameters: AuthParameters): CompletableFuture<AuthResponse>

    /**
     * Provides all possible ways to call EmailMagicLinks endpoints
     */

    public interface EmailMagicLinks {
        /**
         * Data class used for wrapping parameters used with MagicLinks.EmailMagicLinks.loginOrCreate
         * @property email is the account identifier for the account in the form of an Email address where you wish to
         * receive a magic link to authenticate
         * @property loginMagicLinkUrl is the url where you should be redirected for login
         * @property signupMagicLinkUrl is the url where you should be redirected for signup
         * @property loginExpirationMinutes is the duration after which the login url should expire
         * @property signupExpirationMinutes is the duration after which the signup url should expire
         * @property loginTemplateId Use a custom template for login emails. By default, it will use your default email
         * template. The template must be a template using our built-in customizations or a custom HTML email for
         * Magic links - Login.
         * @property signupTemplateId Use a custom template for sign-up emails. By default, it will use your default
         * email template. The template must be a template using our built-in customizations or a custom HTML email for
         * Magic links - Sign-up.
         * @property locale Used to determine which language to use when sending the user this delivery method.
         * Currently supported languages are English (`"en"`), Spanish (`"es"`), and Brazilian Portuguese (`"pt-br"`);
         * if no value is provided, the copy defaults to English.
         */
        @Parcelize
        @JacocoExcludeGenerated
        public data class Parameters
            @JvmOverloads
            constructor(
                val email: String,
                val loginMagicLinkUrl: String? = null,
                val signupMagicLinkUrl: String? = null,
                val loginExpirationMinutes: Int? = null,
                val signupExpirationMinutes: Int? = null,
                val loginTemplateId: String? = null,
                val signupTemplateId: String? = null,
                val locale: Locale? = null,
            ) : Parcelable

        /**
         * Send either a login or signup magic link to the user based on if the email is associated with a user already.
         * A new or pending user will receive a signup magic link. An active user will receive a login magic link.
         * @param parameters required to receive magic link
         * @return [BaseResponse]
         */
        public suspend fun loginOrCreate(parameters: Parameters): BaseResponse

        /**
         * Send either a login or signup magic link to the user based on if the email is associated with a user already.
         * A new or pending user will receive a signup magic link. An active user will receive a login magic link.
         * @param parameters required to receive magic link
         * @param callback a callback that receives a [BaseResponse]
         */
        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: BaseResponse) -> Unit,
        )

        /**
         * Send either a login or signup magic link to the user based on if the email is associated with a user already.
         * A new or pending user will receive a signup magic link. An active user will receive a login magic link.
         * @param parameters required to receive magic link
         * @return [BaseResponse]
         */
        public fun loginOrCreateCompletable(parameters: Parameters): CompletableFuture<BaseResponse>

        /**
         * Send a magic link to an existing Stytch user using their email address. If you'd like to create a user and
         * send them a magic link by email with one request, use [loginOrCreate]
         * @param parameters required to receive magic link
         * @return [BaseResponse]
         */
        public suspend fun send(parameters: Parameters): BaseResponse

        /**
         * Send a magic link to an existing Stytch user using their email address. If you'd like to create a user and
         * send them a magic link by email with one request, use [loginOrCreate]
         * @param parameters required to receive magic link
         * @param callback a callback that receives a [BaseResponse]
         */
        public fun send(
            parameters: Parameters,
            callback: (response: BaseResponse) -> Unit,
        )

        /**
         * Send a magic link to an existing Stytch user using their email address. If you'd like to create a user and
         * send them a magic link by email with one request, use [loginOrCreate]
         * @param parameters required to receive magic link
         * @return [BaseResponse]
         */
        public fun sendCompletable(parameters: Parameters): CompletableFuture<BaseResponse>
    }
}
