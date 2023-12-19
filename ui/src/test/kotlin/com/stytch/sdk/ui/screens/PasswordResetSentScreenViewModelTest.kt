package com.stytch.sdk.ui.screens

import androidx.lifecycle.SavedStateHandle
import com.stytch.sdk.consumer.StytchClient
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Before
import java.security.KeyStore

@OptIn(ExperimentalCoroutinesApi::class)
internal class PasswordResetSentScreenViewModelTest {
    private val savedStateHandle = SavedStateHandle()
    private val dispatcher = UnconfinedTestDispatcher()

    @MockK
    private lateinit var mockStytchClient: StytchClient

    private lateinit var viewModel: PasswordResetSentScreenViewModel

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true, true)
        viewModel = PasswordResetSentScreenViewModel(savedStateHandle, mockStytchClient)
    }

    @After
    fun after() {
        unmockkAll()
    }
}