package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.map

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobicom.s18.toledo.aaronace.sidequest.data.QuestRepository
import com.mobicom.s18.toledo.aaronace.sidequest.data.GeocodingService
import com.mobicom.s18.toledo.aaronace.sidequest.data.GeocodingResult
import com.mobicom.s18.toledo.aaronace.sidequest.model.QuestModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class QuestLocation(
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val quests: List<QuestModel>
)

enum class LocationSelectionState {
    SEARCHING,
    MAP_WITH_FORM
}

class MapViewModel : ViewModel() {
    private val geocodingService = GeocodingService()
    private val questRepository = QuestRepository()

    // Get logged-in user ID
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // UI State management
    private val _uiState = mutableStateOf(LocationSelectionState.SEARCHING)
    val uiState: State<LocationSelectionState> = _uiState

    // Quest creation states
    private val _newQuestTitle = mutableStateOf("")
    val newQuestTitle: State<String> = _newQuestTitle

    private val _newQuestDetails = mutableStateOf("")
    val newQuestDetails: State<String> = _newQuestDetails

    // Location states
    private val _selectedLocationResult = mutableStateOf<GeocodingResult?>(null)
    val selectedLocationResult: State<GeocodingResult?> = _selectedLocationResult

    // Search states
    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> = _isSearching

    private var searchJob: Job? = null

    private val _searchResults = mutableStateOf<List<GeocodingResult>>(emptyList())
    val searchResults: State<List<GeocodingResult>> = _searchResults

    // Quest display states
    val userQuests: StateFlow<List<QuestModel>> = questRepository.getUserQuests(currentUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Quest locations derived from user quests
    val questLocations: StateFlow<List<QuestLocation>> = userQuests
        .map { quests ->
            val activeQuests = quests.filter { !it.completed }
            activeQuests
                .groupBy { "${it.latitude},${it.longitude}" }
                .map { (_, questsAtLocation) ->
                    val firstQuest = questsAtLocation.first()
                    QuestLocation(
                        latitude = firstQuest.latitude,
                        longitude = firstQuest.longitude,
                        locationName = firstQuest.location,
                        quests = questsAtLocation
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _selectedQuestLocation = mutableStateOf<QuestLocation?>(null)
    val selectedQuestLocation: State<QuestLocation?> = _selectedQuestLocation

    // Error and success states
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _successMessage = mutableStateOf<String?>(null)
    val successMessage: State<String?> = _successMessage

    // Last created quest states
    private val _lastCreatedQuestLocation = mutableStateOf<GeocodingResult?>(null)
    val lastCreatedQuestLocation: State<GeocodingResult?> = _lastCreatedQuestLocation

    // Functions
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query

        searchJob?.cancel()

        if (query.length > 2) {
            _isSearching.value = true

            searchJob = viewModelScope.launch {
                delay(500)
                searchLocations(query.trim())
            }
        } else {
            _searchResults.value = emptyList()
            _isSearching.value = false
        }
    }

    private suspend fun searchLocations(query: String) {
        if (query != _searchQuery.value.trim()) {
            _isSearching.value = false
            return
        }

        try {
            _isSearching.value = true
            val results = geocodingService.forwardGeocode(query)

            if (query == _searchQuery.value.trim()) {
                _searchResults.value = results
                _isSearching.value = false
            }
        } catch (e: Exception) {
            if (query == _searchQuery.value.trim()) {
                _searchResults.value = emptyList()
                _isSearching.value = false
            }
        }
    }

    fun selectLocation(result: GeocodingResult) {
        _selectedLocationResult.value = result
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _uiState.value = LocationSelectionState.MAP_WITH_FORM
    }

    fun goBackToSearch() {
        _uiState.value = LocationSelectionState.SEARCHING
        _selectedLocationResult.value = null
    }

    fun updateNewQuestTitle(title: String) {
        _newQuestTitle.value = title
    }

    fun updateNewQuestDetails(details: String) {
        _newQuestDetails.value = details
    }

    fun canCreateQuest(): Boolean {
        return _newQuestTitle.value.isNotBlank() &&
                _newQuestDetails.value.isNotBlank() &&
                _selectedLocationResult.value != null
    }

    fun createQuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val locationResult = _selectedLocationResult.value

            if (currentUserId.isEmpty()) {
                _errorMessage.value = "User not logged in"
                return@launch
            }

            if (locationResult == null) {
                _errorMessage.value = "Please select a location"
                return@launch
            }

            val quest = QuestModel(
                title = _newQuestTitle.value,
                details = _newQuestDetails.value,
                location = locationResult.shortName,
                latitude = locationResult.latitude,
                longitude = locationResult.longitude,
                userId = currentUserId
            )

            val result = questRepository.createQuest(quest, currentUserId)

            if (result.isSuccess) {
                _successMessage.value = "Quest Created!"
                _lastCreatedQuestLocation.value = locationResult
                onSuccess()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to create quest"
            }
        }
    }

    // Get active quests grouped by location
    fun getQuestLocations(): List<QuestLocation> {
        return questLocations.value
    }

    fun onQuestMarkerClick(questLocation: QuestLocation) {
        _selectedQuestLocation.value = questLocation
    }

    fun dismissAvailableQuests() {
        _selectedQuestLocation.value = null
    }

    fun resetQuestCreation() {
        _newQuestTitle.value = ""
        _newQuestDetails.value = ""
        _selectedLocationResult.value = null
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _uiState.value = LocationSelectionState.SEARCHING
    }

    fun completeQuest(quest: QuestModel) {
        viewModelScope.launch {
            val result = questRepository.completeQuest(quest.id, currentUserId)
            if (result.isSuccess) {
                // Update the selected quest location to reflect the change immediately
                _selectedQuestLocation.value?.let { currentLocation ->
                    val updatedQuests = currentLocation.quests.map { q ->
                        if (q.id == quest.id) q.copy(completed = true) else q
                    }.filter { !it.completed } // Remove completed quests from the list

                    if (updatedQuests.isEmpty()) {
                        _selectedQuestLocation.value = null
                    } else {
                        _selectedQuestLocation.value = currentLocation.copy(quests = updatedQuests)
                    }
                }
                _successMessage.value = "Quest completed!"
            } else {
                _errorMessage.value = "Failed to complete quest"
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun clearLastCreatedQuestLocation() {
        _lastCreatedQuestLocation.value = null
    }
}