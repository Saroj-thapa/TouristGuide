package com.example.touristguide.data.network

import com.example.touristguide.data.model.PlacesSearchResponse
import com.example.touristguide.data.model.PlaceSearchFeature
import com.example.touristguide.data.model.PlaceSearchProperties
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

// Only Retrofit interfaces below. All data models are in Place.kt

interface GeoapifyService {
    @GET("v1/geocode/reverse")
    fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("apiKey") apiKey: String
    ): Call<PlacesSearchResponse>
}

interface GeoapifyApiService {
    @GET("v2/places")
    suspend fun searchPlaces(
        @Query("categories") categories: String,
        @Query("filter") filter: String,
        @Query("bias") bias: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int = 0,
        @Query("apiKey") apiKey: String,
        @Query("conditions") conditions: String? = null,
        @Query("name") name: String? = null
    ): PlacesSearchResponse
}

