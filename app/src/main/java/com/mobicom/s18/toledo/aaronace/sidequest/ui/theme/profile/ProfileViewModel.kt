package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.profile

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobicom.s18.toledo.aaronace.sidequest.data.sampleQuests
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _username = mutableStateOf("User")
    val username: State<String> = _username

    private val _userRank = mutableStateOf("Novice")
    val userRank: State<String> = _userRank

    private val _completedQuests= mutableStateOf(
        sampleQuests.count { it.completed }
    )
    val completedQuests: State<Int> = _completedQuests

    private fun calculateRank(completedQuests: Int): String {
        return when {
            completedQuests >= 50 -> "Pilot"
            completedQuests >= 20 -> "Voyager"
            completedQuests >= 10 -> "Ranger"
            completedQuests >= 5 -> "Roamer"
            else -> "Newbie"
        }
    }
}