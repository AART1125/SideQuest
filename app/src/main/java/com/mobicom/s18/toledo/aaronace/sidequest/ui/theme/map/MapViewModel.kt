package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.map

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobicom.s18.toledo.aaronace.sidequest.data.QuestRepository
import com.mobicom.s18.toledo.aaronace.sidequest.model.QuestModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class MapViewModel : ViewModel() {
    private val questRepository = QuestRepository()

    private val _newQuestTitle = mutableStateOf("")
    val newQuestTitle: State<String> = _newQuestTitle

    private val _newQuestDetails = mutableStateOf("")
    val newQuestDetails: State<String> = _newQuestDetails

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _successMessage = mutableStateOf<String?>(null)
    val successMessage: State<String?> = _successMessage

    fun updateNewQuestTitle(title: String) {
        _newQuestTitle.value = title
    }

    fun updateNewQuestDetails(details: String) {
        _newQuestDetails.value = details
    }

    fun canCreateQuest(): Boolean {
        return _newQuestTitle.value.isNotBlank() && _newQuestDetails.value.isNotBlank()
    }

    fun createQuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            if (currentUserId.isEmpty()) {
                _errorMessage.value = "User not logged in"
                return@launch
            }

            val quest = QuestModel(
                title = _newQuestTitle.value,
                details = _newQuestDetails.value,
                location = "placeholder",
                userId = currentUserId
            )

            val result = questRepository.createQuest(quest, currentUserId)

            if (result.isSuccess) {
                _successMessage.value = "Quest created!"
                onSuccess()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to create quest"
            }
        }
    }

    fun resetQuestCreation() {
        _newQuestTitle.value = ""
        _newQuestDetails.value = ""
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}