package com.mobicom.s18.toledo.aaronace.sidequest.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

data class GeocodingResult(
    val displayName: String,
    val shortName: String,
    val latitude: Double,
    val longitude: Double
)

class GeocodingService {
    private var lastRequestTime = 0L
    private val minRequestInterval = 200L

    // Reverse geocoding (coordinates to address)
    suspend fun reverseGeocode(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude&zoom=18&addressdetails=1"
                val response = URL(url).readText()
                val json = JSONObject(response)
                json.optString("display_name", null)
            } catch (e: Exception) {
                null
            }
        }
    }

    // Forward geocoding (address to coordinates)
    suspend fun forwardGeocode(query: String): List<GeocodingResult> {
        return withContext(Dispatchers.IO) {
            try {
                val currentTime = System.currentTimeMillis()
                val timeSinceLastRequest = currentTime - lastRequestTime
                if (timeSinceLastRequest < minRequestInterval) {
                    delay(minRequestInterval - timeSinceLastRequest)
                }
                lastRequestTime = System.currentTimeMillis()

                val encodedQuery = URLEncoder.encode(query, "UTF-8")

                // Philippines-focused query
                val url = "https://nominatim.openstreetmap.org/search?" +
                        "format=json" +
                        "&q=$encodedQuery" +
                        "&countrycodes=ph" +
                        "&bounded=1" +
                        "&viewbox=116.0,21.0,127.0,4.0" +
                        "&addressdetails=1" +
                        "&limit=8" +
                        "&dedupe=1" +
                        "&extratags=1"

                val connection = URL(url).openConnection()
                connection.setRequestProperty("User-Agent", "SideQuest-Android-App/1.0")

                val response = connection.getInputStream().bufferedReader().readText()
                val jsonArray = JSONArray(response)

                val results = mutableListOf<GeocodingResult>()
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val displayName = item.getString("display_name")
                    val shortName = extractShortName(item, displayName)

                    results.add(
                        GeocodingResult(
                            displayName = displayName,
                            shortName = shortName,
                            latitude = item.getString("lat").toDouble(),
                            longitude = item.getString("lon").toDouble()
                        )
                    )
                }
                results
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private fun extractShortName(json: JSONObject, fallbackDisplayName: String): String {
        return when {
            json.has("name") && json.getString("name").isNotEmpty() -> {
                json.getString("name")
            }

            json.has("amenity") && json.getString("amenity").isNotEmpty() -> {
                val amenityName = json.optString("name", "")
                if (amenityName.isNotEmpty()) amenityName else json.getString("amenity")
            }

            json.has("address") -> {
                val address = json.getJSONObject("address")
                when {
                    address.has("amenity") -> address.getString("amenity")
                    address.has("building") -> address.getString("building")
                    address.has("shop") -> address.getString("shop")
                    address.has("office") -> address.getString("office")
                    address.has("tourism") -> address.getString("tourism")
                    address.has("leisure") -> address.getString("leisure")
                    address.has("place") -> address.getString("place")
                    else -> parseDisplayNameForShort(fallbackDisplayName)
                }
            }

            else -> parseDisplayNameForShort(fallbackDisplayName)
        }
    }

    private fun parseDisplayNameForShort(displayName: String): String {
        val parts = displayName.split(",").map { it.trim() }

        if (parts.isEmpty()) return displayName

        val firstPart = parts[0]

        val genericTerms = setOf(
            "Unnamed Road", "Road", "Street", "Avenue",
            "Barangay", "Municipality", "City", "Province",
            "Building", "House", "Structure"
        )

        return if (firstPart.length > 3 && !genericTerms.any { firstPart.contains(it, ignoreCase = true) }) {
            firstPart
        } else {
            if (parts.size > 1) {
                val secondPart = parts[1]
                if (secondPart.length > 3 && !genericTerms.any { secondPart.contains(it, ignoreCase = true) }) {
                    secondPart
                } else {
                    firstPart
                }
            } else {
                firstPart
            }
        }
    }
}