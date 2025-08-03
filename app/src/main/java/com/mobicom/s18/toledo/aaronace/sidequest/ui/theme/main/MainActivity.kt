package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.mobicom.s18.toledo.aaronace.sidequest.navigation.AppNavigation
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.SideQuestTheme
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.auth.AuthViewModel
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.profile.ProfileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val authViewModel: AuthViewModel by viewModels()
        val profileViewModel : ProfileViewModel by viewModels()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SideQuestTheme {
                AppNavigation( profileViewModel = profileViewModel, authViewModel = authViewModel)
            }
        }
    }
}

