package com.stytch.sdk.api.requests

class VerifyTokenRequest(
  val ip_match_required: Boolean? = null,
  val user_agent_match_required: Boolean? = null
): BasicRequest(){
}