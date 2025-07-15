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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import com.example.touristguide.viewmodel.LocationViewModel
import com.example.touristguide.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource

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
    val places = viewModel.places.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val userLat = locationViewModel.latitude.collectAsState().value
    val userLon = locationViewModel.longitude.collectAsState().value
    val lat = userLat ?: Constants.NEPAL_LAT
    val lon = userLon ?: Constants.NEPAL_LON

    val error = firebaseViewModel.errorState.collectAsState().value
    // Fetch data when user location is available
    LaunchedEffect(lat, lon) {
        if (lat != null && lon != null) {
            viewModel.fetchTouristPlaces(lat, lon)
            firebaseViewModel.loadUserPlans(userId)
        }
    }
    RequestLocationIfNeededForPlaces(locationViewModel)
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
            var searchQuery by remember { mutableStateOf("") }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search places...") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
            Spacer(Modifier.height(16.dp))
            // Show which location is being used
            val usingDefaultLocation = userLat == null || userLon == null
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = if (usingDefaultLocation) Color.Gray else Color(0xFF388E3C))
                Spacer(Modifier.width(8.dp))
                if (usingDefaultLocation) {
                    Text("Places In Nepal", color = Color.Gray)
                } else {
                    Text("Showing places near your location", color = Color(0xFF388E3C))
                }
            }
            // Filtered list
            val filteredPlaces = places.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true) ||
                        it.address.contains(searchQuery, ignoreCase = true)
            }
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredPlaces.isEmpty()) {
                Text("No places found.", color = Color.Gray, modifier = Modifier.padding(16.dp))
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    filteredPlaces.forEach { place ->
                        PlaceCard(
                            place = place,
                            userId = userId,
                            firebaseViewModel = firebaseViewModel,
                            placeReviews = placeReviews
                        )
                        Spacer(Modifier.height(12.dp))
                    }
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

@Composable
fun PlaceCard(
    place: com.example.touristguide.data.model.Place,
    userId: String,
    firebaseViewModel: com.example.touristguide.viewmodel.FirebaseViewModel,
    placeReviews: List<Map<String, Any>>
) {
    var reviewText by remember { mutableStateOf("") }
    var reviewRating by remember { mutableStateOf("") }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(Modifier.background(Color(0xFFF8F8FF)).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color(0xFF607D8B),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(place.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(place.address, fontSize = 14.sp, color = Color.Gray)
                    if (place.category.isNotBlank()) {
                        Text(place.category, fontSize = 13.sp, color = Color(0xFF3F51B5))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            // Save to Plan Button
            Button(onClick = {
                val planId = "plan_${System.currentTimeMillis()}"
                val planData = mapOf(
                    "userId" to userId,
                    "lat" to place.latitude,
                    "lon" to place.longitude,
                    "name" to place.name,
                    "address" to place.address
                )
                firebaseViewModel.savePlan(planId, planData)
            }) {
                Text("Save to My Plans")
            }
            Spacer(Modifier.height(8.dp))
            // Reviews Section
            Text("Community Reviews", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            if (place.name.isNotBlank()) {
                LaunchedEffect(place.name) {
                    firebaseViewModel.loadPlaceReviews(place.name)
                }
            }
            placeReviews.filter { it["placeId"] == place.name }.forEach { review ->
                val user = review["userId"] as? String ?: "User"
                val text = review["text"] as? String ?: ""
                val rating = review["rating"] as? String ?: ""
                Text("$user: $text ($rating)", fontSize = 14.sp)
            }
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
                    label = { Text("Rating") },
                    modifier = Modifier.width(80.dp)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    if (place.name.isNotBlank() && reviewText.isNotBlank() && reviewRating.isNotBlank()) {
                        val reviewId = "review_${System.currentTimeMillis()}"
                        val reviewData = mapOf(
                            "placeId" to place.name,
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
            Spacer(Modifier.height(8.dp))
            // Bookmark Button
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val bookmarkId = "bookmark_${System.currentTimeMillis()}"
                    val bookmarkData = mapOf(
                        "userId" to userId,
                        "lat" to place.latitude,
                        "lon" to place.longitude,
                        "name" to place.name,
                        "address" to place.address
                    )
                    firebaseViewModel.addBookmark(userId, bookmarkId, bookmarkData)
                }) {
                    Text("Bookmark")
                }
                Button(onClick = { /* Add to Plan logic if needed */ }, colors = ButtonDefaults.buttonColors(Color.Black)) {
                    Text("Add to Plan", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun RequestLocationIfNeededForPlaces(locationViewModel: com.example.touristguide.viewmodel.LocationViewModel) {
    val context = LocalContext.current
    val latitude by locationViewModel.latitude.collectAsState()
    val longitude by locationViewModel.longitude.collectAsState()

    LaunchedEffect(Unit) {
        if (latitude == null || longitude == null) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location: Location? ->
                location?.let {
                    locationViewModel.updateLocation(it.latitude, it.longitude)
                }
            }
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
