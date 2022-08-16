package com.stytch.sdk.network

import com.stytch.sdk.StytchLog
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

internal class SessionCookieJar: CookieJar {

    private var cookies: MutableList<Cookie> = mutableListOf()

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        if (url.encodedPath().endsWith("login")) {
            this.cookies = ArrayList(cookies)
        }
        StytchLog.d("Test saveFromResponse: $cookies")
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        StytchLog.d("Test saveFromResponse: $cookies")
        if (!url.encodedPath().endsWith("login")) {
            return cookies
        }
        return mutableListOf();
    }
}