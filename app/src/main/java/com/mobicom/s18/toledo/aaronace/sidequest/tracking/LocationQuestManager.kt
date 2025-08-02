package com.mobicom.s18.toledo.aaronace.sidequest.tracking

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobicom.s18.toledo.aaronace.sidequest.data.QuestRepository
import com.mobicom.s18.toledo.aaronace.sidequest.model.QuestModel
import com.mobicom.s18.toledo.aaronace.sidequest.tracking.QuestNotificationManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LocationQuestManager(context: Context) : ViewModel() {

    private val locationService = LocationService(context)
    private val notificationManager = QuestNotificationManager(context)
    private val questRepository = QuestRepository()

    // Get logged-in user ID
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Get user's active quests
    private val userQuests: StateFlow<List<QuestModel>> = questRepository.getUserQuests(currentUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    // Combine location and quests to check for proximity
    private val locationQuestCombined = combine(
        locationService.currentLocation,
        userQuests
    ) { location, quests ->
        location to quests.filter { !it.completed } // Only check active quests
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null to emptyList()
    )

    init {
        // Start monitoring location and quests
        viewModelScope.launch {
            locationQuestCombined.collect { (location, activeQuests) ->
                location?.let { userLocation ->
                    locationService.checkQuestProximity(
                        userLocation = userLocation,
                        activeQuests = activeQuests,
                        onQuestsNearby = { nearbyQuests ->
                            notificationManager.showQuestLocationNotification(nearbyQuests)
                        }
                    )
                }
            }
        }
    }

    fun startLocationTracking() {
        locationService.startLocationTracking()
    }

    fun stopLocationTracking() {
        locationService.stopLocationTracking()
    }

    fun hasNotificationPermission(): Boolean {
        return notificationManager.hasNotificationPermission()
    }

    fun getCurrentLocation(callback: (android.location.Location?) -> Unit) {
        locationService.getCurrentLocation(callback)
    }

    val isTracking: StateFlow<Boolean> = locationService.isTracking
    val currentLocation = locationService.currentLocation

    override fun onCleared() {
        super.onCleared()
        stopLocationTracking()
    }
}