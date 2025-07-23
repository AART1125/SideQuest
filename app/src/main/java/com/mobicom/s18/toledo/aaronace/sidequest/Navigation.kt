package com.mobicom.s18.toledo.aaronace.sidequest

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobicom.s18.toledo.aaronace.sidequest.screens.LandingScreen
import com.mobicom.s18.toledo.aaronace.sidequest.screens.LoginScreen
import com.mobicom.s18.toledo.aaronace.sidequest.screens.MainScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "landing"
    ) {
        composable("landing") {
            LandingScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("landing") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("login") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("main") {
            MainScreen()
        }
    }
}