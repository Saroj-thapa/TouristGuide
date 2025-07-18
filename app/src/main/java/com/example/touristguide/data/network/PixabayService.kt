package com.example.touristguide.data.network

import retrofit2.http.GET
import retrofit2.http.Query

// Data models for Pixabay API

data class PixabayResponse(
    val total: Int,
    val totalHits: Int,
    val hits: List<PixabayImage>
)

data class PixabayImage(
    val id: Int,
    val pageURL: String,
    val tags: String,
    val previewURL: String,
    val webformatURL: String,
    val largeImageURL: String,
    val user: String,
    val userImageURL: String
)

interface PixabayService {
    @GET("api/")
    suspend fun searchImages(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("image_type") imageType: String = "photo",
        @Query("per_page") perPage: Int = 3,
        @Query("safesearch") safeSearch: Boolean = true
    ): PixabayResponse
} 