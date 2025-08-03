package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.profile

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobicom.s18.toledo.aaronace.sidequest.data.UserRepository
import com.mobicom.s18.toledo.aaronace.sidequest.data.sampleQuests
import com.mobicom.s18.toledo.aaronace.sidequest.model.UserModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val currentUser = auth.currentUser?.uid

    private val _username = mutableStateOf("User")
    val username: State<String> = _username

    private val _userRank = mutableStateOf("Novice")
    val userRank: State<String> = _userRank

    private val _completedQuests= mutableStateOf(0)
    val completedQuests: State<Int> = _completedQuests

    init {
        if (currentUser != null && currentUser.isNotEmpty()){
            viewModelScope.launch {
                UserRepository().getUserData(currentUser)
                    .onStart {
                        _username.value = "Loading..."
                    }
                    .catch { e ->
                        _username.value = "Error loading username"
                    }
                    .collect { user ->
                        if (user?.totalCompletedQuests == 5 || user?.totalCompletedQuests == 10 || user?.totalCompletedQuests == 20 || user?.totalCompletedQuests == 50 ){
                            user.calculateRank()
                            UserRepository().updateUserData(currentUser, user)
                        }
                        _username.value = user?.username as String
                        _userRank.value = user?.rank as String
                        _completedQuests.value = user?.totalCompletedQuests as Int

                    }
            }
        }
    }
}