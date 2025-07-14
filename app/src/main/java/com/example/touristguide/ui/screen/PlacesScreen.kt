package com.example.touristguide.ui.screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.PlacesViewModel
import com.example.touristguide.utils.Constants
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import com.example.touristguide.viewmodel.LocationViewModel
import com.example.touristguide.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesScreen(
    navController: NavController,
    viewModel: PlacesViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel(),
    firebaseViewModel: FirebaseViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val userPlans = firebaseViewModel.userPlans.collectAsState().value
    val placeReviews = firebaseViewModel.placeReviews.collectAsState().value
    val geoapifyData = viewModel.geoapifyData.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val lat = locationViewModel.latitude.collectAsState().value
    val lon = locationViewModel.longitude.collectAsState().value
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
            com.google.android.gms.maps.model.LatLng(lat ?: Constants.NEPAL_LAT, lon ?: Constants.NEPAL_LON), 12f
        )
    }
    val error = firebaseViewModel.errorState.collectAsState().value
    // Fetch data when user location is available
    LaunchedEffect(lat, lon) {
        if (lat != null && lon != null) {
            viewModel.fetchPlaceInfo(lat, lon)
            firebaseViewModel.loadUserPlans(userId)
        }
    }
    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Places to Visit",
                navController = navController
            )
        },
        bottomBar = {
            CommonBottomBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues).verticalScroll(scrollState)
        ) {
            if (error != null) {
                Text(
                    text = "Error: $error",
                    color = Color.Red,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
            }
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            var searchQuery by remember { mutableStateOf("") }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search places, food, hotels...") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.LightGray, RoundedCornerShape(24.dp))
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Destination Title", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Location", fontSize = 14.sp, color = Color.Gray)
                }
            }
            Spacer(Modifier.height(16.dp))
            // Place Info Section
            val placeProps = geoapifyData?.features?.firstOrNull()?.properties
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Photo (placeholder)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // You can replace with a real image if you have a URL
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = placeProps?.name ?: "Unknown Place",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = placeProps?.amenity ?: placeProps?.city ?: placeProps?.country ?: "Info not available",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            // Map and Address Section
            Text("Location", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            val address = placeProps?.formatted
            if (address != null) {
                Text("Address: $address", fontSize = 14.sp, color = Color.DarkGray)
            } else {
                Text("Loading address...", fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(Modifier.height(8.dp))
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
                    // Marker for current place
                    Marker(
                        state = com.google.maps.android.compose.MarkerState(
                            position = com.google.android.gms.maps.model.LatLng(lat ?: Constants.NEPAL_LAT, lon ?: Constants.NEPAL_LON)
                        ),
                        title = placeProps?.name ?: "Location"
                    )
                    // Markers for user plans
                    userPlans.forEach { plan ->
                        val planLat = plan["lat"] as? Double
                        val planLon = plan["lon"] as? Double
                        val planName = plan["name"] as? String ?: "Saved Place"
                        if (planLat != null && planLon != null) {
                            Marker(
                                state = com.google.maps.android.compose.MarkerState(
                                    position = com.google.android.gms.maps.model.LatLng(planLat, planLon)
                                ),
                                title = planName
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            // Save to Plan Button
            Button(onClick = {
                if (lat != null && lon != null && placeProps != null) {
                    val planId = "plan_${System.currentTimeMillis()}"
                    val planData = mapOf(
                        "userId" to userId,
                        "lat" to lat,
                        "lon" to lon,
                        "name" to (placeProps.name ?: "Unknown Place"),
                        "address" to (placeProps.formatted ?: "")
                    )
                    firebaseViewModel.savePlan(planId, planData)
                }
            }) {
                Text("Save to My Plans")
            }
            Spacer(Modifier.height(16.dp))
            // Reviews Section
            Text("Community Reviews", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            // Show reviews
            if (placeProps?.name != null) {
                LaunchedEffect(placeProps.name) {
                    firebaseViewModel.loadPlaceReviews(placeProps.name)
                }
            }
            placeReviews.forEach { review ->
                val user = review["userId"] as? String ?: "User"
                val text = review["text"] as? String ?: ""
                val rating = review["rating"] as? String ?: ""
                Text("$user: $text ($rating)", fontSize = 14.sp)
            }
            // Add review
            var reviewText by remember { mutableStateOf("") }
            var reviewRating by remember { mutableStateOf("") }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Write a review") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = reviewRating,
                    onValueChange = { reviewRating = it },
                    label = { Text("Rating",fontSize = 15.sp) },
                    modifier = Modifier.width(80.dp)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    if (placeProps?.name != null && reviewText.isNotBlank() && reviewRating.isNotBlank()) {
                        val reviewId = "review_${System.currentTimeMillis()}"
                        val reviewData = mapOf(
                            "placeId" to placeProps.name,
                            "userId" to userId,
                            "text" to reviewText,
                            "rating" to reviewRating
                        )
                        firebaseViewModel.saveReview(reviewId, reviewData)
                        reviewText = ""
                        reviewRating = ""
                    }
                }) {
                    Text("Post")
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Description", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Row {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Overview", fontWeight = FontWeight.Bold)
                    Text("Description of the selected destination...")
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Community Ratings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RatingCard(user = "User123", review = "Great place to visit!", rating = "★★★★★")
                RatingCard(user = "Traveler456", review = "Highly recommend!", rating = "★★★★☆")
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if (lat != null && lon != null && placeProps != null) {
                        val bookmarkId = "bookmark_${System.currentTimeMillis()}"
                        val bookmarkData = mapOf(
                            "userId" to userId,
                            "lat" to lat,
                            "lon" to lon,
                            "name" to (placeProps.name ?: "Unknown Place"),
                            "address" to (placeProps.formatted ?: "")
                        )
                        firebaseViewModel.addBookmark(userId, bookmarkId, bookmarkData)
                    }
                }) {
                    Text("Bookmark")
                }
                Button(onClick = { }, colors = ButtonDefaults.buttonColors(Color.Black)) {
                    Text("Add to Plan", color = Color.White)
                }
            }
            Spacer(Modifier.height(16.dp))
            // Display user's bookmarks
            Text("My Bookmarks", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            LaunchedEffect(userId) { firebaseViewModel.loadBookmarks(userId) }
            if (firebaseViewModel.bookmarks.collectAsState().value.isEmpty()) {
                Text("No bookmarks yet.", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                firebaseViewModel.bookmarks.collectAsState().value.forEach { bookmark ->
                    val name = bookmark["name"] as? String ?: "Unknown"
                    val address = bookmark["address"] as? String ?: ""
                    Text("$name - $address", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun RatingCard(user: String, review: String, rating: String) {
    Card(
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(user, fontWeight = FontWeight.Bold)
            Text(rating, color = Color(0xFFFFD700))
            Text(review)
        }
    }
}

@Composable
fun TransportOption(label: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color.LightGray, RoundedCornerShape(4.dp))
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, fontWeight = FontWeight.Bold)
            Text(description, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun InfoBox(title: String, value: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
            .padding(16.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlacesScreenPreview() {
    TouristGuideTheme {
        val fakeNavController = rememberNavController()
        PlacesScreen(navController = fakeNavController)
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PlacesScreenLightPreview() {
    TouristGuideTheme(darkTheme = false) {
        val fakeNavController = rememberNavController()
        PlacesScreen(navController = fakeNavController)
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun PlacesScreenDarkPreview() {
    TouristGuideTheme(darkTheme = true) {
        val fakeNavController = rememberNavController()
        PlacesScreen(navController = fakeNavController)
    }
}
