package com.stytch.sdk.utils

import com.google.gson.JsonParser
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.fail

private fun RecordedRequest.verify(
    expectedMethod: String,
    expectedPath: String,
    expectedBody: Map<String, Any?>,
) {
    assert(method == expectedMethod)
    assert(path == expectedPath)
    var bodyString = body.readUtf8()
    if (bodyString.isEmpty()) {
        bodyString = "{}"
    }
    val bodyJson = JsonParser.parseString(bodyString).asJsonObject
    expectedBody.forEach { (key, value) ->
        try {
            assert(bodyJson.get(key).asString == value.toString())
        } catch (_: NullPointerException) {
            fail("Key '$key' was missing in the request body")
        } catch (_: AssertionError) {
            fail("Expected $value but got ${bodyJson.get(key)}")
        } catch (e: Exception) {
            fail(e.message)
        }
    }
}

internal fun RecordedRequest.verifyPost(
    expectedPath: String,
    expectedBody: Map<String, Any?> = emptyMap()
) = verify("POST", expectedPath, expectedBody)

internal fun RecordedRequest.verifyGet(
    expectedPath: String
) = verify("GET", expectedPath, emptyMap())

internal fun RecordedRequest.verifyDelete(
    expectedPath: String,
) = verify("DELETE", expectedPath, emptyMap())
