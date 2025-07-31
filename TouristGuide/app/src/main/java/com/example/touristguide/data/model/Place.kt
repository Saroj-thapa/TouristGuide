package com.example.touristguide.data.model

import org.osmdroid.util.GeoPoint

// Place/Category enums
enum class PlaceCategory(val apiCategory: String, val displayName: String) {
    ACCOMMODATION("accommodation", "Hotels & Accommodations"),
    RESTAURANTS("catering.restaurant", "Restaurants"),
    HOSPITALS("healthcare.hospital", "Hospitals"),
    MEDICAL("healthcare.pharmacy", "Pharmacies"),
    TOURIST_ATTRACTIONS("tourism.attraction", "Tourist Attractions")
}

data class Place(
    val id: String,
    val name: String?,
    val address: String?,
    val category: PlaceCategory,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String? = null,
    val rating: Double? = null,
    val price: Double? = null
)

// For Geoapify API response

data class PlacesSearchResponse(
    val features: List<PlaceSearchFeature> = emptyList()
)

data class PlaceSearchFeature(
    val properties: PlaceSearchProperties = PlaceSearchProperties()
)

data class PlaceSearchProperties(
    val name: String? = null,
    val street: String? = null,
    val city: String? = null,
    val postcode: String? = null,
    val country: String? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val formatted: String = "",
    val categories: List<String> = emptyList(),
    val phone: String? = null,
    val website: String? = null
)
