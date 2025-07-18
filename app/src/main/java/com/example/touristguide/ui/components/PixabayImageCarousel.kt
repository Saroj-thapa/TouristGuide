package com.example.touristguide.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.touristguide.data.network.PixabayService
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.padding

@Composable
fun PixabayImageCarousel(
    query: String,
    apiKey: String,
    pixabayService: PixabayService,
    modifier: Modifier = Modifier,
    maxResults: Int = 5
) {
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            isLoading = true
            imageUrls = try {
                withContext(Dispatchers.IO) {
                    pixabayService.searchImages(apiKey, query, perPage = maxResults).hits.map { it.webformatURL }
                }
            } catch (e: Exception) {
                emptyList()
            }
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (imageUrls.isNotEmpty()) {
        LazyRow(modifier = modifier.fillMaxWidth().height(180.dp)) {
            items(imageUrls) { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
} 