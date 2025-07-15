package com.example.touristguide.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

// Data models for Geoapify response
// You can expand these as needed for your use case

data class GeoapifyResponse(
    val features: List<Feature>
)

data class Feature(
    val properties: Properties
)

data class Properties(
    val formatted: String?,
    val country: String?,
    val city: String?,
    val street: String?,
    val housenumber: String?,
    val lat: Double?,
    val lon: Double?,
    val name: String?,
    val postcode: String?,
    val state: String?,
    val district: String?,
    val suburb: String?,
    val amenity: String?,
    // Added fields for richer restaurant info
    val cuisine: String?,
    val opening_hours: String?,
    val website: String?,
    val phone: String?
)

interface GeoapifyService {
    @GET("v1/geocode/reverse")
    fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("apiKey") apiKey: String
    ): Call<GeoapifyResponse>

    // New: Search for places (restaurants, food, etc.) near a location
    @GET("v2/places")
    fun searchNearbyPlaces(
        @Query("categories") categories: String, // e.g., "catering.restaurant,catering.fast_food"
        @Query("filter") filter: String, // e.g., "circle:lon,lat,radius"
        @Query("limit") limit: Int = 20,
        @Query("apiKey") apiKey: String
    ): Call<GeoapifyResponse>
}

