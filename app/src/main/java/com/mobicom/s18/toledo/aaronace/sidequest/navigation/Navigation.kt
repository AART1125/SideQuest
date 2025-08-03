package com.mobicom.s18.toledo.aaronace.sidequest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.auth.AuthViewModel
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.landing.LandingScreen
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.auth.LoginScreen
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.auth.SignupScreen
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.main.MainScreen
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by authViewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(uiState.isLoggedIn, currentRoute) {
        if (!uiState.isLoggedIn && currentRoute == "main") {
            navController.navigate("login") {
                popUpTo("main") { inclusive = true } // Clear back stack up to main
                launchSingleTop = true
            }
        }
    }


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
                },
                authViewModel = authViewModel
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
            MainScreen(authViewModel = authViewModel)
        }
    }
}