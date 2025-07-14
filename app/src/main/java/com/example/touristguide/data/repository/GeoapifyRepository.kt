package com.example.touristguide.data.repository

import com.example.touristguide.data.network.GeoapifyResponse
import com.example.touristguide.data.network.RetrofitInstance
import retrofit2.Call

class GeoapifyRepository {
    private val apiKey = "859de3c5e42a4c309377f7eb1f9ddb00"

    fun getPlaceInfo(lat: Double, lon: Double): Call<GeoapifyResponse> {
        return RetrofitInstance.api.reverseGeocode(lat, lon, apiKey)
    }
}