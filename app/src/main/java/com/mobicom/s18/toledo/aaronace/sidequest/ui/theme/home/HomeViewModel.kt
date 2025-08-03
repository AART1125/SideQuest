package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.mobicom.s18.toledo.aaronace.sidequest.data.QuestRepository
import com.mobicom.s18.toledo.aaronace.sidequest.data.UserRepository
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

    var showRankUp by mutableStateOf(false)
        private set

    fun triggerRankUpPopup() {
        showRankUp = true
    }

    fun closeRankUpPopup() {
        showRankUp = false
    }

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

    private val _quests = mutableStateOf<List<QuestModel>>(emptyList())
    val questsState: State<List<QuestModel>> = _quests

    // Keep collecting from repository to stay in sync
    init {
        viewModelScope.launch {
            questRepository.getUserQuests(currentUserId)
                .onEach { questsFromRepo ->
                    _isLoading.value = false
                    _quests.value = questsFromRepo
                }
                .collect { }
        }
    }

    fun selectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun getActiveQuests(): List<QuestModel> {
        return _quests.value.filter { !it.completed }
    }

    fun getCompletedQuests(): List<QuestModel> {
        return _quests.value.filter { it.completed }
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
            UserRepository().getUserData(currentUserId)
                .collect {
                    if (it?.totalCompletedQuests == 5 || it?.totalCompletedQuests == 10 || it?.totalCompletedQuests == 20 || it?.totalCompletedQuests == 50) {
                        triggerRankUpPopup()
                        _selectedQuest.value = null
                    }
                }
        }
    }

    fun selectQuest(quest: QuestModel?) {
        _selectedQuest.value = quest
    }

    fun deleteQuest(quest: QuestModel) {
        viewModelScope.launch {
            _quests.value = _quests.value.filter { it.id != quest.id }
            questRepository.deleteQuest(quest.id)
        }
    }
}