package com.example.touristguide.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.HotTub
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.touristguide.viewmodel.LocationViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.touristguide.R
import kotlin.math.roundToInt
import androidx.compose.ui.layout.ContentScale
import com.example.touristguide.ui.theme.*
import com.example.touristguide.viewmodel.HotelsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.data.model.Hotel
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.clickable
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.touristguide.data.network.PixabayService
import coil.compose.AsyncImage
import com.example.touristguide.ui.components.PixabayImageCarousel
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(
    hotelId: String,
    navController: NavController,
    hotelsViewModel: HotelsViewModel,
    locationViewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current
    val hotels by hotelsViewModel.hotels.collectAsState()
    val userLat by locationViewModel.latitude.collectAsState()
    val userLon by locationViewModel.longitude.collectAsState()
    val isFavorite by hotelsViewModel.isFavorite.collectAsState()
    val hotel = hotels.find { it.id == hotelId } ?: Hotel(
        id = hotelId,
        name = "Unknown Hotel",
        location = "Unknown Location",
        price = 2000.0, // Use Double
        rating = 3.0,   // Use Double
        lat = 27.7172,
        lon = 85.3240,
        isFavorite = false,
        phone = null,
        website = null,
        amenities = listOf("Wi-Fi", "Hot Water", "Parking")
    )
    // Pixabay dependencies
    val pixabayApiKey = "51346711-84695fc7acb617448e6e8b140"
    val pixabayRetrofit = remember {
        retrofit2.Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
    }
    val pixabayService = remember { pixabayRetrofit.create(PixabayService::class.java) }
    LaunchedEffect(hotelId) {
        hotelsViewModel.selectHotel(hotelId)
    }
    val distance = if (userLat != null && userLon != null) {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(userLat!!, userLon!!, hotel.lat, hotel.lon, results)
        (results[0] / 1000).roundToInt()
    } else null
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(hotel.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color(0xFFF8FAFB)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Remove the placeholder image Box, only keep the Pixabay image carousel
            PixabayImageCarousel(
                query = hotel.name ?: "",
                apiKey = pixabayApiKey,
                pixabayService = pixabayService
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(hotel.name ?: "", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text(hotel.location ?: "", fontSize = 15.sp, color = Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                val rating = hotel.rating?.toInt() ?: 0
                repeat(rating) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                }
                repeat(5 - rating) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                }
                if (distance != null) {
                    Text("  •  $distance km away", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Rating: ${hotel.rating?.toString() ?: "N/A"}", fontSize = 15.sp)
            Text("Price: ${hotel.price?.toString() ?: "N/A"}", fontSize = 15.sp)
            Text("Facilities", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                hotel.amenities.forEach {
                    val icon = when (it.lowercase()) {
                        "wi-fi", "wifi" -> Icons.Filled.Wifi
                        "hot water" -> Icons.Filled.HotTub
                        "parking" -> Icons.Filled.LocalParking
                        else -> Icons.Filled.Wifi
                    }
                    Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = {
                    val gmmIntentUri = android.net.Uri.parse("google.navigation:q=${hotel.lat},${hotel.lon}")
                    val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                }) {
                    Text("Open in Maps")
                }
                Button(onClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId == null) {
                        Toast.makeText(context, "Please log in to save favorites", Toast.LENGTH_SHORT).show()
                    } else {
                        if (isFavorite) hotelsViewModel.removeHotelFromFavorites(hotel.id)
                        else hotelsViewModel.saveHotelToFavorites(hotel)
                    }
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Filled.Star,
                        contentDescription = if (isFavorite) "Saved" else "Save",
                        tint = if (isFavorite) Color(0xFFFFC107) else Color.Gray
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (isFavorite) "Saved" else "Save")
                }
                if (!hotel.website.isNullOrBlank() && hotel.website.startsWith("http")) {
                    Button(onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(hotel.website))
                        context.startActivity(intent)
                    }) {
                        Text("Visit Website")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Contact Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                if (!hotel.phone.isNullOrBlank() && hotel.phone!!.any { it.isDigit() }) {
                    Text(
                        text = hotel.phone ?: "N/A",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:${hotel.phone}"))
                            context.startActivity(intent)
                        }
                    )
                } else {
                    Text("N/A", fontSize = 15.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Language, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(hotel.website ?: "N/A", fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            // (Optional) Reviews section placeholder
            Text("Reviews (coming soon)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
        }
    }
}
