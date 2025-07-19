package com.example.touristguide.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.touristguide.data.model.Hotel
import com.example.touristguide.ui.components.CommonBottomBar
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.theme.*
import com.example.touristguide.viewmodel.HotelsViewModel
import com.example.touristguide.viewmodel.LocationViewModel
import com.example.touristguide.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.touristguide.ui.components.PlaceMap
import org.osmdroid.util.GeoPoint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import android.util.Log
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelsScreen(
    navController: NavController,
    hotelsViewModel: HotelsViewModel,
    locationViewModel: LocationViewModel = viewModel(),
    firebaseViewModel: FirebaseViewModel = viewModel()
) {
    val context = LocalContext.current
    // Location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    // Get user location (simulate for now if not available)
    val lat = locationViewModel.latitude.collectAsState().value ?: 27.7172
    val lon = locationViewModel.longitude.collectAsState().value ?: 85.3240
    // Fetch hotels when location or filters change
    val radius by hotelsViewModel.radius.collectAsState()
    val openNow by hotelsViewModel.openNow.collectAsState()
    val websiteOnly by hotelsViewModel.websiteOnly.collectAsState()
    val minRating by hotelsViewModel.minRating.collectAsState()
    val priceLevel by hotelsViewModel.priceLevel.collectAsState()
    val amenities by hotelsViewModel.amenities.collectAsState()
    LaunchedEffect(lat, lon, radius, openNow, websiteOnly, minRating, priceLevel, amenities) {
        hotelsViewModel.fetchHotels(lat, lon)
    }
    // UI state
    var searchQuery by remember { mutableStateOf("") }
    val hotels = hotelsViewModel.hotels.collectAsState().value
    val isLoading = hotelsViewModel.isLoading.collectAsState().value
    val error = hotelsViewModel.error.collectAsState().value
    val filteredHotels = hotels.filter {
        (it.name?.contains(searchQuery, ignoreCase = true) == true) ||
        (it.location?.contains(searchQuery, ignoreCase = true) == true)
    }
    // Debug: print hotel IDs
    Log.d("HotelDebug", "Hotel IDs in list: ${hotels.map { it.id }}")
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val bookmarks = firebaseViewModel.bookmarks.collectAsState().value
    // Define hotelPlaces before using it in the Card
    val hotelPlaces = filteredHotels.map {
        com.example.touristguide.data.model.Place(
            id = it.id,
            name = it.name,
            address = it.location,
            latitude = it.lat,
            longitude = it.lon,
            category = com.example.touristguide.data.model.PlaceCategory.ACCOMMODATION,
            imageUrl = null
        )
    }
    // UI
    Scaffold(
        topBar = { CommonTopBar(title = "Explore Hotels", navController = navController) },
        bottomBar = { CommonBottomBar(navController = navController) },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Map at the top (smaller, inside a Card)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
            ) {
                PlaceMap(
                    context = LocalContext.current,
                    places = hotelPlaces,
                    currentLocation = org.osmdroid.util.GeoPoint(lat, lon),
                    modifier = Modifier.fillMaxSize()
                )
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search hotels or locations") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = md_theme_light_primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Modern Filter Bar
            HotelFilterBar(
                openNow = openNow,
                onOpenNowChange = { hotelsViewModel.setOpenNow(it) },
                websiteOnly = websiteOnly,
                onWebsiteOnlyChange = { hotelsViewModel.setWebsiteOnly(it) },
                minRating = minRating,
                onMinRatingChange = { hotelsViewModel.setMinRating(it) },
                priceLevel = priceLevel,
                onPriceLevelChange = { hotelsViewModel.setPriceLevel(it) },
                amenities = amenities,
                onAmenitiesChange = { hotelsViewModel.setAmenities(it) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Text("Error: $error", color = Color.Red)
            } else if (filteredHotels.isEmpty()) {
                Text("No hotels found.", color = Color.Gray)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (!isLoading && hotels.isNotEmpty()) {
                        items(filteredHotels) { hotel ->
                            HotelCard(
                                hotel = hotel,
                                isBookmarked = bookmarks.any { (it["name"] as? String) == hotel.name },
                                onBookmark = {
                                    if (userId == null) {
                                        Toast.makeText(context, "Please log in to bookmark", Toast.LENGTH_SHORT).show()
                                        return@HotelCard
                                    }
                                    val name = hotel.name.orEmpty()
                                    val address = hotel.location.orEmpty()
                                    if (name.isBlank() || address.isBlank()) {
                                        Toast.makeText(context, "Missing hotel name or address", Toast.LENGTH_SHORT).show()
                                        return@HotelCard
                                    }
                                    val bookmarkId = "${name}_${hotel.lat}_${hotel.lon}".replace(".", "_")
                                    val bookmarkData = mapOf(
                                        "name" to name,
                                        "address" to address,
                                        "type" to "HOTEL"
                                    )
                                    try {
                                        firebaseViewModel.addBookmark(userId, bookmarkId, bookmarkData)
                                        android.util.Log.d("BookmarkDebug", "Tried to add hotel bookmark for $userId at $bookmarkId: $bookmarkData")
                                    } catch (e: Exception) {
                                        android.util.Log.e("BookmarkDebug", "Failed to add hotel bookmark: ${e.message}")
                                        Toast.makeText(context, "Failed to bookmark: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onClick = {
                                    Log.d("HotelDebug", "Navigating to hotelDetails/${hotel.id}")
                                    navController.navigate("hotelDetails/${hotel.id}")
                                }
                            )
                        }
                    }
                }
            }
            HotelInfoSummary(filteredHotels)
        }
    }
}

@Composable
fun HotelFilterBar(
    openNow: Boolean,
    onOpenNowChange: (Boolean) -> Unit,
    websiteOnly: Boolean,
    onWebsiteOnlyChange: (Boolean) -> Unit,
    minRating: Int,
    onMinRatingChange: (Int) -> Unit,
    priceLevel: String?,
    onPriceLevelChange: (String?) -> Unit,
    amenities: List<String>,
    onAmenitiesChange: (List<String>) -> Unit
) {
    val scrollState = rememberScrollState()
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(6.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = CloudWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Remove Open now filter UI
            // Website only
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Language, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(16.dp))
                Text("Web", fontSize = 13.sp, modifier = Modifier.padding(start = 2.dp))
                Switch(checked = websiteOnly, onCheckedChange = onWebsiteOnlyChange, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(8.dp))
            // Min rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(16.dp))
                Text("Rating", fontSize = 13.sp, modifier = Modifier.padding(start = 2.dp))
                Slider(
                    value = minRating.toFloat(),
                    onValueChange = { onMinRatingChange(it.toInt()) },
                    valueRange = 0f..5f,
                    steps = 5,
                    modifier = Modifier.width(60.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            // Price chips
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(16.dp))
                PriceChip(label = "Budget", selected = priceLevel == "budget") { onPriceLevelChange(if (priceLevel == "budget") null else "budget") }
                PriceChip(label = "Mid", selected = priceLevel == "mid") { onPriceLevelChange(if (priceLevel == "mid") null else "mid") }
                PriceChip(label = "Luxury", selected = priceLevel == "luxury") { onPriceLevelChange(if (priceLevel == "luxury") null else "luxury") }
            }
            Spacer(Modifier.width(8.dp))
            // Amenities chips
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Wifi, contentDescription = null, tint = LeafGreen, modifier = Modifier.size(16.dp))
                AmenityChip(label = "WiFi", selected = amenities.contains("wifi")) {
                    onAmenitiesChange(if (amenities.contains("wifi")) amenities - "wifi" else amenities + "wifi")
                }
                AmenityChip(label = "Parking", selected = amenities.contains("parking")) {
                    onAmenitiesChange(if (amenities.contains("parking")) amenities - "parking" else amenities + "parking")
                }
                AmenityChip(label = "Breakfast", selected = amenities.contains("breakfast")) {
                    onAmenitiesChange(if (amenities.contains("breakfast")) amenities - "breakfast" else amenities + "breakfast")
                }
            }
        }
    }
}

@Composable
fun PriceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) SkyBlue else Color.LightGray,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable { onClick() }
    ) {
        Text(label, color = if (selected) Color.White else Color.Black, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

@Composable
fun AmenityChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) LeafGreen else Color.LightGray,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable { onClick() }
    ) {
        Text(label, color = if (selected) Color.White else Color.Black, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

@Composable
fun HotelCard(
    hotel: Hotel,
    isBookmarked: Boolean,
    onBookmark: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(SkyBlue, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (hotel.imageUrl != null) {
                    AsyncImage(
                        model = hotel.imageUrl,
                        contentDescription = hotel.name,
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Hotel,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(hotel.name.orEmpty(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                Text(hotel.location.orEmpty(), fontSize = 14.sp, color = TextSecondary)
                Text("Lat: ${hotel.lat}, Lon: ${hotel.lon}", fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Rs. ${hotel.price?.toString() ?: "N/A"}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = md_theme_light_primary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val rating = hotel.rating?.toInt() ?: 0
                    repeat(rating) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(14.dp))
                    }
                    repeat(5 - rating) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    }
                }
                IconButton(onClick = onBookmark) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = if (isBookmarked) "Bookmarked" else "Bookmark",
                        tint = if (isBookmarked) SunsetOrange else Color.LightGray
                    )
                }
            }
        }
    }
}

// Show hotel info summary above the hotel list
@Composable
fun HotelInfoSummary(hotels: List<Hotel>) {
    if (hotels.isNotEmpty()) {
        val avgPrice = hotels.mapNotNull { it.price }.average().toInt() // Use toInt() instead of toIntOrNull()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Hotel, contentDescription = null, tint = md_theme_light_primary, modifier = Modifier.size(18.dp))
            Text(" ${hotels.size} hotels found", fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 4.dp))
            Spacer(Modifier.width(12.dp))
            Icon(Icons.Filled.Star, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(16.dp))
            Text(" Avg. Price: Rs. $avgPrice", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(start = 2.dp))
        }
    }
}
