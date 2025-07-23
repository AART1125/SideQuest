package com.mobicom.s18.toledo.aaronace.sidequest.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import org.osmdroid.util.GeoPoint

class MapViewModel : ViewModel() {
    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    private val _submittedSearch = mutableStateOf<String?>(null)
    val submittedSearch: State<String?> = _submittedSearch

    private val _tappedPoint = mutableStateOf<GeoPoint?>(null)
    val tappedPoint: State<GeoPoint?> = _tappedPoint

    private val _showQuestNotification = mutableStateOf(true)
    val showQuestNotification: State<Boolean> = _showQuestNotification

    private val _newQuestTitle = mutableStateOf("")
    val newQuestTitle: State<String> = _newQuestTitle

    private val _newQuestDetails = mutableStateOf("")
    val newQuestDetails: State<String> = _newQuestDetails

    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    fun submitSearch() {
        _submittedSearch.value = _searchText.value
    }

    fun onMapTap(point: GeoPoint) {
        _tappedPoint.value = point
        _showQuestNotification.value = false
    }

    fun dismissQuestPopup() {
        _showQuestNotification.value = false
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
                _tappedPoint.value != null
    }

    fun resetQuestCreation() {
        _newQuestTitle.value = ""
        _newQuestDetails.value = ""
        _tappedPoint.value = null
    }
}