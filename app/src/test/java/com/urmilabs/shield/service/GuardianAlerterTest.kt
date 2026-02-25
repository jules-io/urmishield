package com.urmilabs.shield.service

import android.content.Context
import android.telephony.SmsManager
import com.urmilabs.shield.analytics.ShieldAnalytics
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.google.firebase.crashlytics.FirebaseCrashlytics

class GuardianAlerterTest {

    private lateinit var guardianAlerter: GuardianAlerter
    private lateinit var mockContext: Context
    private lateinit var mockSmsManager: SmsManager
    private lateinit var mockAnalytics: ShieldAnalytics

    @Before
    fun setup() {
        mockkStatic(FirebaseCrashlytics::class)
        val mockCrashlytics = mockk<FirebaseCrashlytics>(relaxed = true)
        every { FirebaseCrashlytics.getInstance() } returns mockCrashlytics

        mockContext = mockk(relaxed = true)
        mockSmsManager = mockk(relaxed = true)
        mockAnalytics = mockk(relaxed = true)
        every { mockContext.getSystemService(SmsManager::class.java) } returns mockSmsManager
        every { mockContext.getString(com.urmilabs.shield.R.string.urmi_shield_alert, "IRS") } returns "URMI SHIELD ALERT: A potential scam call was blocked. Details: IRS"
        guardianAlerter = GuardianAlerter(mockContext, mockAnalytics)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `does not send SMS when guardian number is empty`() {
        guardianAlerter.sendAlert("", "IRS scam detected")

        verify(exactly = 0) { mockSmsManager.sendTextMessage(any(), any(), any(), any(), any()) }
        verify(exactly = 0) { mockAnalytics.logGuardianAlertSent() }
    }

    @Test
    fun `sends SMS and logs analytics when guardian number is provided`() {
        guardianAlerter.sendAlert("+1234567890", "IRS")

        verify {
            mockSmsManager.sendTextMessage(
                "+1234567890",
                null,
                "URMI SHIELD ALERT: A potential scam call was blocked. Details: IRS",
                null,
                null
            )
        }
        verify { mockAnalytics.logGuardianAlertSent() }
    }

    @Test
    fun `handles SMS exception gracefully without logging analytics`() {
        every { mockSmsManager.sendTextMessage(any(), any(), any(), any(), any()) } throws RuntimeException("No service")

        // Should not throw
        guardianAlerter.sendAlert("+1234567890", "test")

        // Analytics should NOT be called since SMS failed
        verify(exactly = 0) { mockAnalytics.logGuardianAlertSent() }
    }
}
