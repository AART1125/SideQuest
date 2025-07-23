package com.mobicom.s18.toledo.aaronace.sidequest.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.mobicom.s18.toledo.aaronace.sidequest.data.models.QuestModel
import com.mobicom.s18.toledo.aaronace.sidequest.data.sampleQuests

class HomeViewModel : ViewModel() {
    private val _quests = mutableStateOf(sampleQuests.toMutableList())
    val quests: State<MutableList<QuestModel>> = _quests

    private val _selectedQuest = mutableStateOf<QuestModel?>(null)
    val selectedQuest: State<QuestModel?> = _selectedQuest

    fun getActiveQuests(): List<QuestModel> {
        return _quests.value.filter { !it.isCompleted }
    }

    fun deleteQuest(quest: QuestModel) {
        _quests.value = _quests.value.toMutableList().apply {
            removeAll {it.id == quest.id }
        }
    }

    fun completeQuest(quest: QuestModel) {
        _quests.value = _quests.value.map {
            if (it.id == quest.id) {
                it.copy(isCompleted = true)
            } else {
                it
            }
        }.toMutableList()
        _selectedQuest.value = null
    }

    fun selectQuest(quest: QuestModel?) {
        _selectedQuest.value = quest
    }
}