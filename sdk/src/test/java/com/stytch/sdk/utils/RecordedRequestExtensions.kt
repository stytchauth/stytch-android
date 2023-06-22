package com.stytch.sdk.utils

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.fail

private fun compare(key: String, requestValue: JsonElement, expectedValue: Any?) {
    require(expectedValue != null) {
        "Request contained a value for $key but expected was null"
    }
    if (requestValue.isJsonObject) {
        // objects should be deep-compared
        val nestedObject = requestValue.asJsonObject
        require(expectedValue is Map<*, *>) {
            "Request contains an object with keys ${nestedObject.keySet()} but expected $expectedValue"
        }
        require(nestedObject.size() == expectedValue.size) {
            "Request contains different keys (${nestedObject.keySet()}) than expected (${expectedValue.keys})"
        }
        nestedObject.keySet().forEach { nestedKey ->
            val nestedRequestValue = nestedObject.get(nestedKey)
            compare(nestedKey, nestedRequestValue, expectedValue[nestedKey])
        }
    } else if (requestValue.isJsonArray) {
        val array = requestValue.asJsonArray
        require(expectedValue is List<*>) {
            "Request contains a list, but expected was not a list"
        }
        require(array.size() == expectedValue.size) {
            "Request contains ${array.size()} elements but expected ${expectedValue.size}"
        }
        val expectedAsJsonArray = JsonArray(expectedValue.size).apply {
            expectedValue.forEach {
                add(it.toString())
            }
        }
        require(requestValue == expectedAsJsonArray) {
            "Expected $expectedValue but got $requestValue"
        }
    } else {
        // primitives can be string compared
        require(requestValue.asString == expectedValue.toString()) {
            "Expected $expectedValue but got $requestValue"
        }
    }
}

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
    expectedBody.forEach { (key, expectedValue) ->
        val requestValue = bodyJson.get(key)
        try {
            compare(key, requestValue, expectedValue)
            bodyJson.remove(key)
        } catch (_: NullPointerException) {
            fail("Key '$key' was missing in the request body")
        } catch (_: AssertionError) {
            fail("Expected $expectedValue but got $requestValue")
        } catch (e: Exception) {
            fail(e.message)
        }
    }
    assert(bodyJson.size() == 0) {
        "The following keys were found in the request and were unexpected: ${bodyJson.keySet()}"
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

internal fun RecordedRequest.verifyPut(
    expectedPath: String,
    expectedBody: Map<String, Any?> = emptyMap()
) = verify("PUT", expectedPath, expectedBody)