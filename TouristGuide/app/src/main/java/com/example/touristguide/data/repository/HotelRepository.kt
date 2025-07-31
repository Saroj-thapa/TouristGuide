package com.example.touristguide.data.repository

import com.example.touristguide.data.model.Hotel
import com.example.touristguide.data.model.PlacesSearchResponse
import com.example.touristguide.data.network.GeoapifyApiService
import org.osmdroid.util.GeoPoint

class HotelRepository(private val geoapifyService: GeoapifyApiService, private val apiKey: String) {
    suspend fun getNearbyHotels(
        location: GeoPoint?,
        radius: Int = 5000, // meters
        openNow: Boolean = false,
        websiteOnly: Boolean = false,
        minRating: Int = 0,
        priceLevel: String? = null, // "budget", "mid", "luxury" (if you want to support)
        amenities: List<String> = emptyList(), // e.g., ["wifi", "parking"]
        limit: Int = 30
    ): List<Hotel> {
        val lat = location?.latitude ?: 27.7172
        val lon = location?.longitude ?: 85.3240
        val filter = "circle:$lon,$lat,$radius"
        val bias = "proximity:$lon,$lat"
        val conditions = if (openNow) "open_now:true" else null
        val response = geoapifyService.searchPlaces(
            categories = "accommodation.hotel",
            filter = filter,
            bias = bias,
            limit = limit,
            apiKey = apiKey,
            conditions = conditions
        )
        val hotels = response.features.mapIndexedNotNull { index, feature ->
            val props = feature.properties
            // Assign rating and price deterministically based on index for consistent filtering
            val rating = when {
                index < 6 -> 3.0 // budget hotels get rating 3
                index in 6..17 -> 4.0 // mid hotels get rating 4
                else -> 5.0 // luxury hotels get rating 5
            }
            val price = when {
                index < 6 -> (500..1200).random().toDouble() // budget
                index in 6..17 -> (2000..3500).random().toDouble() // mid
                else -> (3501..5000).random().toDouble() // luxury
            }
            val finalPrice = if ((props.name ?: "").contains("Radisson", ignoreCase = true)) {
                (2000..3500).random().toDouble()
            } else price
            Hotel(
                id = "${props.name}_${"%.5f".format(props.lat)}_${"%.5f".format(props.lon)}",
                name = props.name ?: "Unknown Hotel",
                location = props.formatted,
                price = finalPrice,
                rating = rating,
                lat = props.lat,
                lon = props.lon,
                isFavorite = false,
                phone = props.phone ?: "+977-XXXXXXX",
                website = props.website ?: "",
                amenities = listOf("Wi-Fi", "Hot Water", "Parking")
            )
        }.filter { it.rating != null && it.rating!! >= minRating }
        // Now, filter by priceLevel and rating so each group is unique
        return when (priceLevel) {
            "budget" -> hotels.filter { it.rating == 3.0 }.take(6)
            "mid" -> hotels.filter { it.rating == 4.0 }.take(12)
            "luxury" -> hotels.filter { it.rating == 5.0 }.take(7)
            else -> hotels
        }
    }
}
