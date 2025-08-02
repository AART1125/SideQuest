package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.mobicom.s18.toledo.aaronace.sidequest.navigation.AppNavigation
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.SideQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if we should navigate to map from notification
        val shouldNavigateToMap = intent.getBooleanExtra("navigate_to_map", false)

        // Only use shouldNavigateToMap if user is actually logged in
        val auth = FirebaseAuth.getInstance()
        val canNavigateToMap = shouldNavigateToMap && auth.currentUser != null

        setContent {
            SideQuestTheme {
                AppNavigation(shouldNavigateToMap = canNavigateToMap)
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // Handle new intents (when app is already running)
        val shouldNavigateToMap = intent.getBooleanExtra("navigate_to_map", false)
        val auth = FirebaseAuth.getInstance()

        if (shouldNavigateToMap && auth.currentUser != null) {
            recreate()
        }
    }
}