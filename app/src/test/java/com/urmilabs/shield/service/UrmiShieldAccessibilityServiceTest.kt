package com.urmilabs.shield.service

import android.view.accessibility.AccessibilityEvent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.urmilabs.shield.ai.ScamDetector
import com.urmilabs.shield.db.AppDatabase
import com.urmilabs.shield.db.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UrmiShieldAccessibilityServiceTest {

    private lateinit var service: UrmiShieldAccessibilityService
    private lateinit var mockOverlayManager: OverlayManager

    @Before
    fun setup() {
        // Manually instantiate the service and its dependencies for a pure unit test.
        // This avoids all the complexity and brittleness of Hilt's test setup.
        service = UrmiShieldAccessibilityService()
        mockOverlayManager = mockk(relaxed = true)

        // Manually inject the mocked dependencies.
        service.overlayManager = mockOverlayManager
        service.scamDetector = mockk(relaxed = true)
        service.callLogDao = mockk(relaxed = true)
        service.settings = mockk(relaxed = true)
        service.guardianAlerter = mockk(relaxed = true)
        service.remoteConfig = mockk(relaxed = true)
        every { service.remoteConfig.supportedDialerPackages } returns setOf("com.google.android.dialer")
        service.analytics = mockk(relaxed = true)
    }

    @Test
    fun `when call screen appears, overlay is shown`() {
        // Arrange: Create a fake event for the Google Dialer call screen appearing.
        val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
        event.packageName = "com.google.android.dialer"
        event.className = "com.google.android.dialer.call.CallActivity"

        // Act: Send the event to the service.
        service.onAccessibilityEvent(event)

        // Assert: Verify that the showOverlay method was called on our mock.
        verify { mockOverlayManager.showOverlay() }
    }
}
