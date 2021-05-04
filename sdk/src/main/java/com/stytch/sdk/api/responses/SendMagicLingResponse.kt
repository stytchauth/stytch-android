package com.stytch.sdk.api.responses

import com.google.gson.annotations.SerializedName

public class SendMagicLingResponse(
    @SerializedName("request_id") public val request_id: String,
    @SerializedName("user_id") public val user_id: String,
    @SerializedName("user_created") public val user_created: Boolean,
    @SerializedName("email_id") public val email_id: String,
) : BasicResponse() {
}
