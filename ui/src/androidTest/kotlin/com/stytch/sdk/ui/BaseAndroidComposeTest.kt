package com.stytch.sdk.ui

import org.junit.After
import org.junit.Before

internal abstract class BaseAndroidComposeTest {

    abstract fun provideTestInstance(): Any

    @Before
    open fun setUp() {
    }

    @After
    open fun tearDown() {
    }
}
