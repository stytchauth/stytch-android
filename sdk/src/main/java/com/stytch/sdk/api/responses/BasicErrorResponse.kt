package com.stytch.sdk.api.responses

import com.google.gson.annotations.SerializedName

public class BasicErrorResponse(
    @SerializedName("status") public val status: Int,
    @SerializedName("message") public val message: String?,
    @SerializedName("error_type") public val error_type: String?,
    @SerializedName("error_message") public val error_message: String?,
    @SerializedName("error_url") public val error_url: String?,
) {
}
