package com.stytch.sdk.api

import com.google.gson.annotations.SerializedName

public class BasicErrorResponse(
    @SerializedName("status") public val status: Int,
    @SerializedName("message") public val message: String?,
    @SerializedName("error_type") public val error_type: String?,
    @SerializedName("error_message") public val error_message: String?,
    @SerializedName("error_url") public val error_url: String?,
)

public open class BasicResponse

public class CreateUserResponse(
    @SerializedName("request_id") public val request_id: String,
    @SerializedName("user_id") public val user_id: String,
    @SerializedName("email_id") public val email_id: String,
) : BasicResponse()

public class DeleteUserResponse : BasicResponse()

public class SendEmailVerificationResponse(
    @SerializedName("user_id") public val user_id: String,
) : BasicResponse()

public class SendMagicLingResponse(
    @SerializedName("request_id") public val request_id: String,
    @SerializedName("user_id") public val user_id: String,
    @SerializedName("user_created") public val user_created: Boolean,
    @SerializedName("email_id") public val email_id: String,
) : BasicResponse()

public class VerifyTokenResponse(
    @SerializedName("request_id") public val request_id: String,
    @SerializedName("user_id") public val user_id: String,
) : BasicResponse()
