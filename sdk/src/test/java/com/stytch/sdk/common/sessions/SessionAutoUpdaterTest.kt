package com.stytch.sdk.common.sessions

import com.stytch.sdk.common.StytchDispatchers
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class SessionAutoUpdaterTest {
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
        sessionAutoUpdater.startSessionUpdateJob(dispatchers, mockk(), mockk())
        verify { sessionAutoUpdater.stopSessionUpdateJob() }
        assert(sessionAutoUpdater.sessionUpdateJob != null)
        sessionAutoUpdater.stopSessionUpdateJob()
        assert(sessionAutoUpdater.sessionUpdateJob == null)
    }
}
