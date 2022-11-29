package com.stytch.sdk.sessions

import com.stytch.sdk.StytchDispatchers
import com.stytch.sessions.SessionAutoUpdater
import com.stytch.sessions.SessionStorage
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class SessionAutoUpdaterTest {
    @MockK
    private lateinit var mockSessionStorage: SessionStorage
    private lateinit var sessionAutoUpdater: SessionAutoUpdater
    private val dispatchers = StytchDispatchers(Dispatchers.Unconfined, Dispatchers.Unconfined)

    @Before
    fun before() {
        sessionAutoUpdater = spyk()
        MockKAnnotations.init(this, true, true)
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `SessionAutoUpdater stopSessionUpdateJob nulls out job`() {
        sessionAutoUpdater.sessionUpdateJob = Job()
        sessionAutoUpdater.stopSessionUpdateJob()
        assert(sessionAutoUpdater.sessionUpdateJob == null)
    }

    @Test
    fun `SessionAutoUpdater startSessionUpdateJob cancels previously running job and creates new one`() {
        assert(sessionAutoUpdater.sessionUpdateJob == null)
        sessionAutoUpdater.startSessionUpdateJob(dispatchers, mockSessionStorage)
        verify { sessionAutoUpdater.stopSessionUpdateJob() }
        assert(sessionAutoUpdater.sessionUpdateJob != null)
        sessionAutoUpdater.stopSessionUpdateJob()
        assert(sessionAutoUpdater.sessionUpdateJob == null)
    }
}
