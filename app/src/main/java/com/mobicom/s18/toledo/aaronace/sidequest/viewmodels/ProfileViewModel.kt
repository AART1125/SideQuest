package com.mobicom.s18.toledo.aaronace.sidequest.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.mobicom.s18.toledo.aaronace.sidequest.data.sampleQuests

class ProfileViewModel : ViewModel() {
    private val _username = mutableStateOf("User")
    val username: State<String> = _username

    private val _userRank = mutableStateOf("Novice")
    val userRank: State<String> = _userRank

    private val _completedQuests= mutableStateOf(
        sampleQuests.count { it.isCompleted }
    )
    val completedQuests: State<Int> = _completedQuests

    fun updateCompletedQuests() {
        _completedQuests.value = sampleQuests.count { it.isCompleted }
    }
}