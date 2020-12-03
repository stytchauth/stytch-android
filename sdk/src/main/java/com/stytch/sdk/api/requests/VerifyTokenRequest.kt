package com.stytch.sdk.api.requests

import com.google.gson.annotations.SerializedName

public class VerifyTokenRequest(
  @SerializedName("ip_match_required")
  public val ip_match_required: Boolean? = null,
  @SerializedName("user_agent_match_required")
  public val user_agent_match_required: Boolean? = null
): BasicRequest(){
}