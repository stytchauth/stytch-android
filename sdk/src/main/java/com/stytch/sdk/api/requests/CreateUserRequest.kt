package com.stytch.sdk.api.requests

import com.google.gson.annotations.SerializedName

public class CreateUserRequest(
    @SerializedName("email") public val email: String,
): BasicRequest() {
}
