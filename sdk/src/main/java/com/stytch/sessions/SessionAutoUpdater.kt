package com.stytch.sessions

import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchResult
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.responseData.AuthData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow

internal object SessionAutoUpdater {
    private var sessionUpdateJob: Job? = null
    private var n = 0
    private var sessionUpdateDelay: Long = 3000L

    fun startSessionUpdateJob() {
//        prevent multiple update jobs running
        stopSessionUpdateJob()
        sessionUpdateJob = GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                // wait before another update request
                delay(sessionUpdateDelay)
                // request session update from backend
                val sessionResult = updateSession()
                //save session data in SessionStorage if call successfull
                if (sessionResult is StytchResult.Success){
                    sessionResult.saveSession()
                } else {
//                    TODO: handle failed session update request
                }
                // add exponential growth to session update delay, result delays: 3000 + 1000*2^n = 3000 -> 4000 -> 4500 -> 4750 -> ...
                sessionUpdateDelay += (1000.0 / 2.0.pow(n)).toLong()
                n++
            }
        }
    }

    fun stopSessionUpdateJob() {
        sessionUpdateJob?.cancel()
        sessionUpdateJob = null
    }

    private suspend fun updateSession(): StytchResult<AuthData>{
        val result: StytchResult<AuthData>
        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.Sessions.authenticate(
                null,
                StytchClient.sessionStorage.sessionToken,
                StytchClient.sessionStorage.sessionJwt
            )
        }
        return result
    }
}

/**
 * Starts session update in background
 */
internal fun StytchResult<AuthData>.launchSessionUpdater(){
    if(this is StytchResult.Success){
//        save session data
        saveSession()
//        start auto session update
        SessionAutoUpdater.startSessionUpdateJob()
    }
}