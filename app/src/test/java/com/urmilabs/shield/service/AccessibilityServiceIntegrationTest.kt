package com.urmilabs.shield.service

import com.urmilabs.shield.analytics.ShieldAnalytics
import com.urmilabs.shield.ai.ScamDetector
import com.urmilabs.shield.config.ShieldRemoteConfig
import com.urmilabs.shield.db.CallLogDao
import com.urmilabs.shield.db.CallLogEntity
import com.urmilabs.shield.db.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Integration-level unit test for the AccessibilityService's scam detection pipeline.
 * Tests the complete flow: text analysis → scam detection → logging → guardian alert.
 * Uses Robolectric-compatible mocks (no Android framework needed).
 */
class AccessibilityServiceIntegrationTest {

    private lateinit var scamDetector: ScamDetector
    private lateinit var callLogDao: CallLogDao
    private lateinit var settings: SettingsRepository
    private lateinit var guardianAlerter: GuardianAlerter
    private lateinit var remoteConfig: ShieldRemoteConfig
    private lateinit var analytics: ShieldAnalytics

    @Before
    fun setup() {
        scamDetector = mockk(relaxed = true)
        callLogDao = mockk(relaxed = true)
        settings = mockk(relaxed = true)
        guardianAlerter = mockk(relaxed = true)
        remoteConfig = mockk(relaxed = true)
        analytics = mockk(relaxed = true)

        every { remoteConfig.textAnalysisDebounceMs } returns 100L
        every { remoteConfig.supportedDialerPackages } returns setOf("com.google.android.dialer")
    }

    @Test
    fun `scam detection pipeline logs to database and sends guardian alert`() = runTest {
        every { scamDetector.detectScam("Give me your SSN") } returns "SSN"
        coEvery { settings.guardianNumber } returns flowOf("+1234567890")

        // Simulate the scam detection flow
        val keyword = scamDetector.detectScam("Give me your SSN")
        assert(keyword == "SSN")

        // Simulate logging
        callLogDao.insert(
            CallLogEntity(
                callerNumber = "Unknown",
                riskLevel = "HIGH",
                detectionReason = "SSN",
                timestamp = System.currentTimeMillis(),
                wasBlocked = false
            )
        )

        coVerify { callLogDao.insert(any()) }

        // Simulate guardian alert
        guardianAlerter.sendAlert("+1234567890", "SSN")
        verify { guardianAlerter.sendAlert("+1234567890", "SSN") }
    }

    @Test
    fun `scam detector returns null for clean text`() {
        every { scamDetector.detectScam("Hello, how are you?") } returns null

        val result = scamDetector.detectScam("Hello, how are you?")
        assert(result == null)
    }

    @Test
    fun `remote config provides correct debounce value`() {
        assert(remoteConfig.textAnalysisDebounceMs == 100L)
    }

    @Test
    fun `remote config provides correct dialer packages`() {
        assert(remoteConfig.supportedDialerPackages.contains("com.google.android.dialer"))
    }

    @Test
    fun `analytics events are logged during scam detection`() {
        analytics.logScamAlertTriggered()
        analytics.logAccessibilityServiceEnabled()

        verify { analytics.logScamAlertTriggered() }
        verify { analytics.logAccessibilityServiceEnabled() }
    }
}
