package com.mobicom.s18.toledo.aaronace.sidequest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.landing.LandingScreen
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.auth.LoginScreen
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.auth.SignupScreen
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.main.MainScreen
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.profile.ProfilePage

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
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSignupClicked = {
                    navController.navigate("signup") {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("signup") {
            SignupScreen(
                onLoginClicked = {
                    navController.navigate("login") {
                        launchSingleTop = true
                    }
                },
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("signup") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("main") {
            MainScreen(
                onLogout = {
                    navController.navigate("landing") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}