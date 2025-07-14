package com.example.touristguide.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
import androidx.compose.ui.tooling.preview.Preview
import com.example.touristguide.data.model.Hotel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.HotelsViewModel
import com.example.touristguide.utils.Constants
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.touristguide.viewmodel.LocationViewModel
import com.example.touristguide.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth

private val hotels = listOf(
    Hotel("1", "Thamel Guest House", "Thamel, Kathmandu", 1500, 4, 27.7156, 85.3123, true),
    Hotel("2", "Lake View Resort", "Pokhara", 2000, 5, 28.2096, 83.9856, true),
    Hotel("3", "Jungle Lodge", "Chitwan", 1800, 4, 27.5341, 84.4525, true),
    Hotel("4", "Mountain Retreat", "Nagarkot", 2500, 5, 27.7172, 85.5200, true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelsScreen(
    navController: NavController,
    hotelsViewModel: HotelsViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel(),
    firebaseViewModel: FirebaseViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredHotels = hotels.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.location.contains(searchQuery, ignoreCase = true)
    }
    val geoapifyData = hotelsViewModel.geoapifyData.collectAsState().value
    val isLoading = hotelsViewModel.isLoading.collectAsState().value
    val lat = locationViewModel.latitude.collectAsState().value
    val lon = locationViewModel.longitude.collectAsState().value
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
            com.google.android.gms.maps.model.LatLng(lat ?: Constants.NEPAL_LAT, lon ?: Constants.NEPAL_LON), 12f
        )
    }
    // Fetch data when user location is available
    LaunchedEffect(lat, lon) {
        if (lat != null && lon != null) {
            hotelsViewModel.fetchHotelLocation(lat, lon)
        }
    }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val userPlans = firebaseViewModel.userPlans.collectAsState().value
    val bookmarks = firebaseViewModel.bookmarks.collectAsState().value

    Scaffold(
        topBar = {
            CommonTopBar(title = "Explore Hotels", navController = navController)
        },
        bottomBar = {
            CommonBottomBar(navController = navController)
        },
        containerColor = Color(0xFFF3F4F6)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search hotels or locations") },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Hotel Info Section
            val hotelProps = geoapifyData?.features?.firstOrNull()?.properties
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Hotel,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = hotelProps?.name ?: "Unknown Hotel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = hotelProps?.amenity ?: hotelProps?.city ?: hotelProps?.country ?: "Info not available",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Map and Address Section
            Text("Hotel Location", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            val address = hotelProps?.formatted
            if (address != null) {
                Text("Address: $address", fontSize = 14.sp, color = Color.DarkGray)
            } else {
                Text("Loading address...", fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = com.google.maps.android.compose.MarkerState(
                            position = com.google.android.gms.maps.model.LatLng(lat ?: Constants.NEPAL_LAT, lon ?: Constants.NEPAL_LON)
                        ),
                        title = hotelProps?.name ?: "Hotel Location"
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Recommended for you", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredHotels) { hotel ->
                    HotelCard(hotel = hotel) {
                        navController.navigate("hotelDetails/${hotel.id}")
                    }
                }
            }
            // Example: Add Bookmark and Add to Plan buttons for each hotel
            hotels.forEach { hotel ->
                Row {
                    Button(onClick = {
                        val bookmarkId = "bookmark_${System.currentTimeMillis()}"
                        val bookmarkData = mapOf(
                            "userId" to userId,
                            "name" to hotel.name,
                            "address" to hotel.location, // Assuming location is the address for this example
                            "lat" to hotel.lat,
                            "lon" to hotel.lon
                        )
                        firebaseViewModel.addBookmark(userId, bookmarkId, bookmarkData)
                    }) { Text("Bookmark") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        val planId = "plan_${System.currentTimeMillis()}"
                        val planData = mapOf(
                            "userId" to userId,
                            "name" to hotel.name,
                            "address" to hotel.location, // Assuming location is the address for this example
                            "lat" to hotel.lat,
                            "lon" to hotel.lon
                        )
                        firebaseViewModel.savePlan(planId, planData)
                    }) { Text("Add to Plan") }
                }
            }
            // Display user's bookmarks
            Text("My Bookmarked Hotels", fontWeight = FontWeight.Bold)
            if (bookmarks.isEmpty()) {
                Text("No hotel bookmarks yet.", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                bookmarks.forEach { bookmark ->
                    val name = bookmark["name"] as? String ?: "Unknown"
                    val address = bookmark["address"] as? String ?: ""
                    Text("$name - $address")
                }
            }
            // Display user's plans
            Text("My Hotel Plans", fontWeight = FontWeight.Bold)
            if (userPlans.isEmpty()) {
                Text("No saved hotel plans yet.", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                userPlans.forEach { plan ->
                    val name = plan["name"] as? String ?: "Unknown"
                    val address = plan["address"] as? String ?: ""
                    Text("$name - $address")
                }
            }
        }
    }
}

@Composable
fun HotelCard(hotel: Hotel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFBBDEFB), Color(0xFFE3F2FD))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_myplaces),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(hotel.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(hotel.location, fontSize = 14.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Rs. ${hotel.price}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(hotel.rating) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    }
                    repeat(5 - hotel.rating) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    }
                    Text(" /night", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                    if (hotel.isFavorite) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHotelsScreen() {
    TouristGuideTheme {
        HotelsScreen(navController = rememberNavController())
    }
}