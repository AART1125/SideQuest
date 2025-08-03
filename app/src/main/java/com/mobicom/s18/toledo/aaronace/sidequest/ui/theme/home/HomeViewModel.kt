package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.mobicom.s18.toledo.aaronace.sidequest.data.QuestRepository
import com.mobicom.s18.toledo.aaronace.sidequest.model.QuestModel
import com.mobicom.s18.toledo.aaronace.sidequest.data.sampleQuests
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel (
    private val questRepository: QuestRepository = QuestRepository(),
) : ViewModel() {

    // Get logged-in user ID
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""



    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    val quests: StateFlow<List<QuestModel>> = questRepository.getUserQuests(currentUserId)
        .onEach { _isLoading.value = false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    private val _selectedQuest = mutableStateOf<QuestModel?>(null)
    val selectedQuest: State<QuestModel?> = _selectedQuest

    private val _selectedTab = mutableStateOf(0)
    val selectedTab: State<Int> = _selectedTab

    fun selectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun getActiveQuests(): List<QuestModel> {
        Log.d("HomeViewModel", "Current User ID: $currentUserId")
        return quests.value.filter { !it.completed }
    }

    fun getCompletedQuests(): List<QuestModel> {
        return quests.value.filter { it.completed }
    }

    fun getCurrentTabQuests(): List<QuestModel> {
        return when (_selectedTab.value) {
            0 -> getActiveQuests()
            1 -> getCompletedQuests()
            else -> emptyList()
        }
    }

    fun completeQuest(quest: QuestModel) {
        viewModelScope.launch {
            questRepository.completeQuest(quest.id, currentUserId)
            _selectedQuest.value = null
        }
    }

    fun selectQuest(quest: QuestModel?) {
        _selectedQuest.value = quest
    }
}