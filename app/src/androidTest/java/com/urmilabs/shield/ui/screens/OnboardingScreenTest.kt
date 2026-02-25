package com.urmilabs.shield.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OnboardingScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun onboardingDisplaysPermissionRequest() {
        composeTestRule.setContent {
            OnboardingScreen(onPermissionsGranted = {})
        }

        // The first permission step should be displayed
        composeTestRule
            .onNodeWithContentDescription("Allow microphone access for call screening")
            .assertIsDisplayed()
    }

    @Test
    fun microphoneButtonIsDisplayed() {
        composeTestRule.setContent {
            OnboardingScreen(onPermissionsGranted = {})
        }

        composeTestRule
            .onNodeWithText("Allow Microphone")
            .assertIsDisplayed()
    }
}
