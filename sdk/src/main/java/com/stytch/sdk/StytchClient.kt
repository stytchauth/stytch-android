package com.stytch.sdk

import android.net.Uri

/**
 * The entrypoint for all Stytch-related interaction.
 */
public object StytchClient {

    /**
     * Configures the StytchClient, setting the publicToken and hostUrl.
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @param hostUrl This is an https url which will be used as the domain for setting session-token cookies to be sent to your servers on subsequent requests
     */
    public fun configure(publicToken: String, hostUrl: Uri){
        TODO("implement")
    }


//    TODO:("Magic Links")
//    fun loginOrCreate(parameters:completion:),
//    fun authenticate(parameters:completion:)

//    TODO:("Sessions")
//    fun revoke(completion:)
//    fun authenticate(parameters:completion:)

//    TODO:("OTP")
//    fun loginOrCreate(parameters:completion:)
//    fun authenticate(parameters:completion:)

//    TODO("OAuth")
//    TODO("User Management")


}