package com.stytch.sdk

import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
internal class ExampleUnitTest {

    @Test
    fun `assert Stytch fails with uninitialized property access exception when attempting login without setting up configuration`() {
        try {
            Stytch.instance.login("Hi!")
        } catch (e: UninitializedPropertyAccessException) {
            return
        }

        Assert.fail()
    }
}
