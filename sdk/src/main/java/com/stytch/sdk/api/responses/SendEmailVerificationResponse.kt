package com.stytch.sdk.api.responses

import com.google.gson.annotations.SerializedName

public class SendEmailVerificationResponse(
    @SerializedName("user_id")
    public val user_id: String
) : BasicResponse() {
}