package com.stytch.sdk.utils

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.fail

private fun compare(
    key: String,
    requestValue: JsonElement,
    expectedValue: Any?,
) {
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
        val expectedAsJsonArray =
            JsonArray(expectedValue.size).apply {
                expectedValue.forEach {
                    if (it is Map<*, *>) {
                        add(JsonParser.parseString(it.toString()))
                    } else {
                        add(it.toString())
                    }
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
    expectedBody: List<Map<String, Any?>>,
) {
    assert(method == expectedMethod)
    assert(path == expectedPath)
    var bodyString = body.readUtf8()
    if (bodyString.isEmpty()) {
        bodyString = "{}"
    }
    // The below assumes that every list is ALWAYS only 1 element large, because the only request we ever make as a list
    // is an event log, which is the only one that _has_ to be a list. Everything else is just a map that we wrap
    // in a list for testing purposes. This helper will fail if we ever test sending multiple events, but we'll
    // cross that bridge when we get to it.
    val bodyJson =
        try {
            JsonParser.parseString(bodyString).asJsonObject
        } catch (_: IllegalStateException) {
            JsonParser.parseString(bodyString).asJsonArray[0].asJsonObject
        }
    expectedBody[0].forEach { (key, expectedValue) ->
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
    expectedBody: Map<String, Any?> = emptyMap(),
) = verify("POST", expectedPath, listOf(expectedBody))

internal fun RecordedRequest.verifyPost(
    expectedPath: String,
    expectedBody: List<Map<String, Any?>> = emptyList(),
) = verify("POST", expectedPath, expectedBody)

internal fun RecordedRequest.verifyGet(expectedPath: String) = verify("GET", expectedPath, listOf(emptyMap()))

internal fun RecordedRequest.verifyDelete(expectedPath: String) = verify("DELETE", expectedPath, listOf(emptyMap()))

internal fun RecordedRequest.verifyPut(
    expectedPath: String,
    expectedBody: Map<String, Any?> = emptyMap(),
) = verify("PUT", expectedPath, listOf(expectedBody))
