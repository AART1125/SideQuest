package com.mobicom.s18.toledo.aaronace.sidequest.tracking

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.mobicom.s18.toledo.aaronace.sidequest.model.QuestModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationService(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    // Track which quest locations we've already notified about to prevent spam
    private val notifiedLocations = mutableSetOf<String>()

    // Distance threshold (100 meters)
    private val locationThresholdMeters = 100f

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000L // Update every 10 seconds
    ).apply {
        setMinUpdateDistanceMeters(10f) // Only update if moved 10 meters
        setMaxUpdateDelayMillis(30000L)
        setWaitForAccurateLocation(false)
    }.build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                _currentLocation.value = location
            }
        }
    }

    fun startLocationTracking() {
        if (!hasLocationPermission()) {
            Log.e("LocationService", "Location permission not granted")
            return
        }

        try {
            Log.d("LocationService", "Starting location tracking...")
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            _isTracking.value = true

            // Also get last known location immediately
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    Log.d("LocationService", "Got last known location: ${it.latitude}, ${it.longitude}")
                    _currentLocation.value = it
                }
            }
        } catch (securityException: SecurityException) {
            Log.e("LocationService", "Security exception: ${securityException.message}")
            _isTracking.value = false
        }
    }

    fun stopLocationTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _isTracking.value = false
    }

    fun checkQuestProximity(
        userLocation: Location,
        activeQuests: List<QuestModel>,
        onQuestsNearby: (List<QuestModel>) -> Unit
    ) {
        val nearbyQuests = mutableListOf<QuestModel>()

        activeQuests.forEach { quest ->
            if (quest.latitude != 0.0 && quest.longitude != 0.0) {
                val questLocation = Location("quest").apply {
                    latitude = quest.latitude
                    longitude = quest.longitude
                }

                val distance = userLocation.distanceTo(questLocation)

                if (distance <= locationThresholdMeters) {
                    val locationKey = "${quest.latitude},${quest.longitude}"

                    // Only add to nearby quests if we haven't notified about this location recently
                    if (!notifiedLocations.contains(locationKey)) {
                        nearbyQuests.add(quest)
                        notifiedLocations.add(locationKey)
                    }
                }
            }
        }

        if (nearbyQuests.isNotEmpty()) {
            onQuestsNearby(nearbyQuests)
        }

        // Clear notified locations that are now far away to allow re-notification
        clearDistantNotifiedLocations(userLocation, activeQuests)
    }

    private fun clearDistantNotifiedLocations(userLocation: Location, activeQuests: List<QuestModel>) {
        val locationsToRemove = mutableSetOf<String>()

        notifiedLocations.forEach { locationKey ->
            val (lat, lon) = locationKey.split(",")
            val questLocation = Location("quest").apply {
                latitude = lat.toDouble()
                longitude = lon.toDouble()
            }

            val distance = userLocation.distanceTo(questLocation)

            // If user is now more than 200 meters away, allow re-notification
            if (distance > locationThresholdMeters * 2) {
                locationsToRemove.add(locationKey)
            }
        }

        notifiedLocations.removeAll(locationsToRemove)
    }

    fun getCurrentLocation(onLocationReceived: (Location?) -> Unit) {
        if (!hasLocationPermission()) {
            onLocationReceived(null)
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                onLocationReceived(location)
            }.addOnFailureListener {
                onLocationReceived(null)
            }
        } catch (securityException: SecurityException) {
            onLocationReceived(null)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}