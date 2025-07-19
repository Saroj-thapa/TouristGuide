package com.example.touristguide.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.touristguide.viewmodel.FoodViewModel
import com.example.touristguide.viewmodel.LocationViewModel
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
import com.example.touristguide.ui.components.PlaceMap
import com.example.touristguide.utils.Constants
import com.example.touristguide.data.model.Place
import com.google.firebase.auth.FirebaseAuth
import com.example.touristguide.viewmodel.FirebaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.touristguide.data.network.PixabayService
import com.example.touristguide.ui.components.PixabayImageCarousel
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(
    navController: NavController,
    viewModel: FoodViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel(),
    firebaseViewModel: FirebaseViewModel = viewModel()
) {
    val context = LocalContext.current
    val places by viewModel.places.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val userLat by locationViewModel.latitude.collectAsState()
    val userLon by locationViewModel.longitude.collectAsState()
    val lat = userLat ?: Constants.NEPAL_LAT
    val lon = userLon ?: Constants.NEPAL_LON

    // Location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Ensure API fetch is triggered
    LaunchedEffect(lat, lon) {
        if (lat != null && lon != null) {
            viewModel.fetchNearbyRestaurants(lat, lon)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    val cuisines = places.mapNotNull { it.category?.name }.distinct()

    var selectedRestaurant by remember { mutableStateOf<Place?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CommonTopBar(title = "Restaurants & Food", navController = navController) },
        bottomBar = { CommonBottomBar(navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. Search Bar at the top
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search restaurants...", color = MaterialTheme.colorScheme.primary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
            // 2. Map in a Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                val lat = userLat
                val lon = userLon
                val currentLocation = if (lat != null && lon != null) org.osmdroid.util.GeoPoint(lat, lon) else null
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(8.dp)
                ) {
                    PlaceMap(
                        context = context,
                        places = places,
                        currentLocation = currentLocation
                    )
                }
            }
            // 5. Restaurant List
            Text(
                text = "Popular Restaurants",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 4.dp)
            )
            // Filtering logic for restaurant list
            val filteredRestaurants = places.filter { place ->
                searchQuery.isBlank() || (place.name?.contains(searchQuery, ignoreCase = true) == true)
            }
            // Debug: Show number of restaurants fetched
            Text("Fetched: ${places.size} restaurants", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(start = 20.dp, bottom = 2.dp))
            when {
                isLoading -> {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }
                filteredRestaurants.isEmpty() -> {
                    Text("No restaurants found.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                    // Show a Toast for debugging
                    LaunchedEffect(filteredRestaurants) {
                        Toast.makeText(context, "No restaurants found after filtering!", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredRestaurants) { place ->
                            Card(
                                onClick = {
                                    selectedRestaurant = place
                                    showDialog = true
                                },
                                shape = RoundedCornerShape(18.dp),
                                elevation = CardDefaults.cardElevation(6.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Image placeholder
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .background(Color.LightGray, CircleShape)
                                    ) {
                                        if (place.imageUrl != null) {
                                            AsyncImage(
                                                model = place.imageUrl,
                                                contentDescription = place.name,
                                                modifier = Modifier.size(64.dp).clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(place.name.orEmpty(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Text(place.category.toString(), fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                        Text(place.address.orEmpty(), fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = if (place.price != null) "Rs ${place.price}" else "-",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "Rating: ${place.rating?.toString() ?: "N/A"}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = "/person",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Button(
                                            onClick = {
                                                val userId = FirebaseAuth.getInstance().currentUser?.uid
                                                if (userId == null) {
                                                    Toast.makeText(context, "Please log in to bookmark", Toast.LENGTH_SHORT).show()
                                                    return@Button
                                                }
                                                val name = place.name.orEmpty()
                                                val address = place.address.orEmpty()
                                                if (name.isBlank() || address.isBlank()) {
                                                    Toast.makeText(context, "Missing restaurant name or address", Toast.LENGTH_SHORT).show()
                                                    return@Button
                                                }
                                                val bookmarkId = "${name}_${place.latitude}_${place.longitude}".replace(".", "_")
                                                val bookmarkData = mapOf(
                                                    "name" to name,
                                                    "address" to address,
                                                    "type" to "FOOD"
                                                )
                                                try {
                                                    firebaseViewModel.addBookmark(userId, bookmarkId, bookmarkData)
                                                    android.util.Log.d("BookmarkDebug", "Tried to add food bookmark for $userId at $bookmarkId: $bookmarkData")
                                                } catch (e: Exception) {
                                                    android.util.Log.e("BookmarkDebug", "Failed to add food bookmark: ${e.message}")
                                                    Toast.makeText(context, "Failed to bookmark: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text("View Details", color = Color.White, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        RestaurantDetailsDialog(
            restaurant = selectedRestaurant!!,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun RestaurantDetailsDialog(
    restaurant: Place,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    // Pixabay dependencies
    val pixabayApiKey = "51346711-84695fc7acb617448e6e8b140"
    val pixabayRetrofit = remember {
        retrofit2.Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
    }
    val pixabayService = remember { pixabayRetrofit.create(PixabayService::class.java) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        title = { Text(restaurant.name.orEmpty(), fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Map in a Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    PlaceMap(
                        context = context,
                        places = listOf(restaurant),
                        currentLocation = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Add image carousel here
                PixabayImageCarousel(
                    query = restaurant.name ?: "",
                    apiKey = pixabayApiKey,
                    pixabayService = pixabayService
                )
                // Info in a Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Address: ${restaurant.address.orEmpty()}")
                        Text("Category: ${restaurant.category.displayName}")
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val gmmIntentUri = android.net.Uri.parse("geo:${restaurant.latitude},${restaurant.longitude}?q=${android.net.Uri.encode(restaurant.name.orEmpty())}")
                                val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                context.startActivity(mapIntent)
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("Open in Maps", modifier = Modifier.padding(start = 4.dp), color = Color.White)
                        }
                    }
                }
            }
        }
    )
}