package com.urmilabs.shield.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun settingsDisplaysTitle() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            SettingsScreen(navController = navController)
        }

        composeTestRule
            .onNodeWithText("Settings")
            .assertIsDisplayed()
    }

    @Test
    fun stallingSwitchIsDisplayed() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            SettingsScreen(navController = navController)
        }

        composeTestRule
            .onNodeWithText("Enable TTS Stalling")
            .assertIsDisplayed()
    }

    @Test
    fun deepfakeSwitchIsDisplayed() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            SettingsScreen(navController = navController)
        }

        composeTestRule
            .onNodeWithText("Enable Deepfake Detection")
            .assertIsDisplayed()
    }

    @Test
    fun whitelistButtonIsAccessible() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            SettingsScreen(navController = navController)
        }

        composeTestRule
            .onNodeWithContentDescription("Manage whitelist")
            .assertIsDisplayed()
    }
}
