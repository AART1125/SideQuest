package com.mobicom.s18.toledo.aaronace.sidequest.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

data class GeocodingResult(
    val displayName: String,
    val latitude: Double,
    val longitude: Double
)

class GeocodingService {

    // Reverse geocoding: coordinates → address
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
                val encodedQuery = URLEncoder.encode(query, "UTF-8")

                // Philippines-focused query
                val url = "https://nominatim.openstreetmap.org/search?" +
                        "format=json" +
                        "&q=$encodedQuery" +
                        "&countrycodes=ph" +
                        "&bounded=1" +
                        "&viewbox=116.0,21.0,127.0,4.0" +
                        "&addressdetails=1" +
                        "&limit=10"

                val response = URL(url).readText()
                val jsonArray = JSONArray(response)

                val results = mutableListOf<GeocodingResult>()
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    results.add(
                        GeocodingResult(
                            displayName = item.getString("display_name"),
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
}