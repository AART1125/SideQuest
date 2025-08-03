package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Import viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobicom.s18.toledo.aaronace.sidequest.data.UserRepository
import com.mobicom.s18.toledo.aaronace.sidequest.model.UserModel
import kotlinx.coroutines.flow.catch // Import catch for error handling
import kotlinx.coroutines.flow.onStart // Import onStart for initial state
import kotlinx.coroutines.launch // Import launch

class ProfileViewModel : ViewModel() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Holds the UserModel, starts as null
    private val _userModel = mutableStateOf<UserModel?>(null)
    val userModel: State<UserModel?> = _userModel

    // Holds the username, derived from userModel
    private val _username = mutableStateOf<String?>("Loading...") // Initial state for username
    val username: State<String?> = _username

    private val _userRank = mutableStateOf<String?>("Novice") // Assuming rank comes from elsewhere or is static for now
    val userRank: State<String?> = _userRank

    private val _completedQuests = mutableStateOf<Int?>(0)
    val completedQuests: State<Int?> = _completedQuests

    private val _showRankUpPopup = mutableStateOf(false)
    val showRankUpPopup: State<Boolean> = _showRankUpPopup

    fun checkRank (newAmount: Int?, userModel: UserModel?) {
        if (newAmount != null) {
            val milestones = listOf(5, 10, 20, 50) // Define milestones for the popup
            if (milestones.contains(newAmount)) {
                userModel?.calculateRank()
                UserRepository().updateUserData(currentUserId as String, userModel as UserModel)
            }
        }
    }

    init {
        if (currentUserId != null && currentUserId.isNotEmpty()) {
            viewModelScope.launch {
                var previousCompletedQuests: Int? = null
                UserRepository().getUserData(currentUserId)
                    .onStart {
                        // You could set a loading state for the whole userModel here if needed
                        _username.value = "Loading..."
                        previousCompletedQuests = _completedQuests.value
                    }
                    .catch { exception ->
                        // Handle any errors during data fetching
                        _username.value = "Error loading username"
                        // Log the exception or show a message to the user
                    }
                    .collect { user ->
                        _userModel.value = user // Store the whole user model if needed elsewhere
                        _username.value = userModel.value?.username
                        _completedQuests.value = userModel.value?.totalCompletedQuests

                        if (_completedQuests.value != null &&
                            (previousCompletedQuests == null || _completedQuests.value!! > previousCompletedQuests!!)) {
                            checkRank(_completedQuests.value, userModel.value)
                        }
                        _userRank.value = userModel.value?.rank
                    }


            }
        } else {
            _username.value = "Not logged in" // Or handle appropriately
            _userModel.value = null
        }
    }
}
