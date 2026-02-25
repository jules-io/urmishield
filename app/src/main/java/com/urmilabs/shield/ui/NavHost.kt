package com.urmilabs.shield.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.urmilabs.shield.ui.screens.DashboardScreen
import com.urmilabs.shield.ui.screens.HistoryScreen
import com.urmilabs.shield.ui.screens.OnboardingScreen
import com.urmilabs.shield.ui.screens.SettingsScreen
import com.urmilabs.shield.ui.screens.NumberListScreen
import com.urmilabs.shield.ui.screens.SystemStatusScreen

@Composable
fun ShieldNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "onboarding",
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable("onboarding") {
            OnboardingScreen(onPermissionsGranted = {
                navController.navigate("dashboard") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("dashboard") {
            DashboardScreen(navController = navController)
        }
        composable("history") {
            HistoryScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("number_list/{type}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "WHITELIST"
            NumberListScreen(type = type, navController = navController)
        }
        composable("guardian_settings") {
            GuardianSettings(navController = navController)
        }
        composable("system_status") {
            SystemStatusScreen(navController = navController)
        }
    }
}
