package com.stytch.sdk.api.responses

import com.google.gson.annotations.SerializedName

public class CreateUserResponse(
    @SerializedName("request_id") public val request_id: String,
    @SerializedName("user_id") public val user_id: String,
    @SerializedName("email_id") public val email_id: String,
) : BasicResponse() {
}
