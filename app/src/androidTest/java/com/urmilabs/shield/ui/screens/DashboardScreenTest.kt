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
class DashboardScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun dashboardDisplaysProtectionStatus() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            DashboardScreen(navController = navController)
        }

        composeTestRule
            .onNodeWithText("Protected Status: Active")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardDisplaysScreenedCount() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            DashboardScreen(navController = navController)
        }

        composeTestRule
            .onNodeWithContentDescription("Total calls screened: 0")
            .assertIsDisplayed()
    }

    @Test
    fun settingsButtonExists() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            DashboardScreen(navController = navController)
        }

        composeTestRule
            .onNodeWithContentDescription("Open settings")
            .assertIsDisplayed()
    }

    @Test
    fun historyButtonExists() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            DashboardScreen(navController = navController)
        }

        composeTestRule
            .onNodeWithContentDescription("View call history")
            .assertIsDisplayed()
    }
}
