package com.stytch.sdk

private const val PREFERENCES_NAME_SESSION_JWT = "session_jwt"
private const val PREFERENCES_NAME_SESSION_TOKEN = "session_token"

internal class SessionStorage {
    var sessionToken: String?
        private set(value) {
            StytchClient.storageHelper.saveValue(PREFERENCES_NAME_SESSION_TOKEN, value)
        }
        get() {
            return StytchClient.storageHelper.loadValue(PREFERENCES_NAME_SESSION_TOKEN)
        }

    var sessionJwt: String?
    private set(value) {
        StytchClient.storageHelper.saveValue(PREFERENCES_NAME_SESSION_JWT, value)
    }
    get() {
        return StytchClient.storageHelper.loadValue(PREFERENCES_NAME_SESSION_JWT)
    }

    fun updateSession(sessionToken: String?, sessionJwt: String?){
        this.sessionToken = sessionToken
        this.sessionJwt = sessionJwt
    }

    fun revoke(){
        sessionToken = null
        sessionJwt = null
    }

}