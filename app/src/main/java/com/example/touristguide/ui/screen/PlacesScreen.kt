package com.example.touristguide.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.touristguide.data.model.Place
import com.example.touristguide.viewmodel.FirebaseViewModel
import com.example.touristguide.viewmodel.LocationViewModel
import com.example.touristguide.viewmodel.PlacesViewModel
import com.example.touristguide.viewmodel.PlacesViewModelFactory
import com.example.touristguide.data.model.PlaceCategory
import com.example.touristguide.data.network.GeoapifyApiService
import com.example.touristguide.data.repository.GeoapifyRepository
import com.example.touristguide.ui.components.CommonBottomBar
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.PlaceMap
import com.example.touristguide.ui.components.PlaceList
import com.example.touristguide.ui.components.PlaceDetailsDialog
import com.example.touristguide.utils.Constants
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.Directions
import androidx.compose.foundation.horizontalScroll
import com.example.touristguide.ui.components.PlaceItemCard
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.viewinterop.AndroidView
import com.example.touristguide.data.network.PixabayService
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.touristguide.ui.components.PixabayImageCarousel
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesScreen(
    navController: NavController,
    locationViewModel: LocationViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    firebaseViewModel: FirebaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    placeName: String? = null
) {
    // --- Provide dependencies for the ViewModel ---
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://api.geoapify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val geoapifyService = remember { retrofit.create(GeoapifyApiService::class.java) }
    val apiKey = "6acbf75b57b74b749fd87b61351b7c77" // <-- Replace with your actual API key
    val repository = remember { GeoapifyRepository(geoapifyService, apiKey) }
    val factory = remember { PlacesViewModelFactory(repository, PlaceCategory.TOURIST_ATTRACTIONS) }
    val viewModel: PlacesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val places = viewModel.searchResults.value
    val isLoading = viewModel.isLoading.value
    val hasMoreData = viewModel.hasMore
    val userLat = locationViewModel.latitude.collectAsState().value
    val userLon = locationViewModel.longitude.collectAsState().value
    val lat = userLat ?: Constants.NEPAL_LAT
    val lon = userLon ?: Constants.NEPAL_LON
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val currentLocation = if (userLat != null && userLon != null) GeoPoint(userLat, userLon) else null

    val coroutineScope = rememberCoroutineScope()
    var selectedPlace by remember { mutableStateOf<com.example.touristguide.data.model.Place?>(null) }
    var placeDetails by remember { mutableStateOf<com.example.touristguide.data.model.Place?>(null) }
    var isDetailsLoading by remember { mutableStateOf(false) }

    val pixabayApiKey = "51346711-84695fc7acb617448e6e8b140"
    val pixabayRetrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val pixabayService = remember { pixabayRetrofit.create(PixabayService::class.java) }
    val imageCache = remember { mutableStateMapOf<String, String?>() }

    // Open dialog for placeName if provided
    LaunchedEffect(placeName, places) {
        if (placeName != null && places.isNotEmpty()) {
            val decoded = java.net.URLDecoder.decode(placeName, "UTF-8")
            val match = places.find { it.name == decoded }
            if (match != null) {
                selectedPlace = match
                isDetailsLoading = false
            }
        }
    }

    // Fetch tourist places for the center of Nepal on first load
    LaunchedEffect(Unit) {
        viewModel.fetchInitialPlaces(28.3949, 84.1240, 50000)
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.searchPlaces() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar above map
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.searchPlaces(it)
                    },
                    label = { Text("Search places...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
            }
            // Map inside Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                PlaceMap(
                    context = context,
                    places = places,
                    currentLocation = currentLocation,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Location info row
            val usingDefaultLocation = userLat == null || userLon == null
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = if (usingDefaultLocation) Color.Gray else Color(0xFF388E3C))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (usingDefaultLocation) "Places In Nepal" else "Showing places near your location",
                    color = if (usingDefaultLocation) Color.Gray else Color(0xFF388E3C),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(8.dp))
            // Places list
            if (isLoading) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (places.isEmpty()) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No places found.", color = Color.Gray, fontSize = 18.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(places) { place ->
                        // Pixabay image fetch logic
                        val imageUrl = imageCache[place.name]
                        LaunchedEffect(place.name) {
                            if (imageUrl == null && !imageCache.containsKey(place.name)) {
                                coroutineScope.launch {
                                    try {
                                        // Build a more specific query for Pixabay
                                        val query = buildString {
                                            append(place.name ?: "")
                                            if (!place.category.displayName.isNullOrBlank()) {
                                                append(" ")
                                                append(place.category.displayName)
                                            }
                                            append(" Nepal")
                                        }
                                        val response = pixabayService.searchImages(
                                            apiKey = pixabayApiKey,
                                            query = query
                                        )
                                        val url = response.hits.firstOrNull()?.webformatURL
                                        imageCache[place.name ?: ""] = url
                                    } catch (e: Exception) {
                                        imageCache[place.name ?: ""] = null
                                    }
                                }
                            }
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlace = place
                                    isDetailsLoading = true
                                    placeDetails = null
                                    coroutineScope.launch {
                                        // Simulate API call for details (replace with real API call if available)
                                        // For now, just use the same place after a delay
                                        kotlinx.coroutines.delay(500)
                                        placeDetails = place // Replace with fetched details
                                        isDetailsLoading = false
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                // Show Pixabay image if available
                                if (imageUrl != null) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = "Place Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        text = "Image from Pixabay",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color(0xFF607D8B),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(place.name ?: "", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Text(place.address ?: "", fontSize = 14.sp, color = Color.Gray)
                                        Text(place.category.displayName, fontSize = 13.sp, color = Color(0xFF3F51B5))
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                                            if (userId == null) {
                                                Toast.makeText(context, "Please log in to bookmark", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            val name = place.name ?: ""
                                            val address = place.address ?: ""
                                            if (name.isBlank() || address.isBlank()) {
                                                Toast.makeText(context, "Missing place name or address", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            val bookmarkId = "${name}_${place.latitude}_${place.longitude}".replace(".", "_")
                                            val bookmarkData = mapOf(
                                                "name" to name,
                                                "address" to address,
                                                "type" to "PLACE"
                                            )
                                            try {
                                                firebaseViewModel.addBookmark(userId, bookmarkId, bookmarkData)
                                                android.util.Log.d("BookmarkDebug", "Tried to add bookmark for $userId at $bookmarkId: $bookmarkData")
                                            } catch (e: Exception) {
                                                android.util.Log.e("BookmarkDebug", "Failed to add bookmark: ${e.message}")
                                                Toast.makeText(context, "Failed to bookmark: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF607D8B))
                                    ) {
                                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color.White)
                                        Spacer(Modifier.width(4.dp))
                                        Text("Bookmark", color = Color.White)
                                    }
                                    Button(
                                        onClick = { /* Add to Plan logic if needed */ },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                        Spacer(Modifier.width(4.dp))
                                        Text("Add to Plan", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                    item {
                        if (isLoading) {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else if (hasMoreData) {
                            Button(
                                onClick = {
                                    viewModel.fetchMorePlaces()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Load More")
                            }
                        }
                    }
                }
            }
        }
        // Details Dialog
        if (selectedPlace != null) {
            AlertDialog(
                onDismissRequest = { selectedPlace = null },
                confirmButton = {
                    TextButton(onClick = { selectedPlace = null }) { Text("Close") }
                },
                title = { Text(placeDetails?.name ?: selectedPlace?.name ?: "Place Details", fontWeight = FontWeight.Bold) },
                text = {
                    if (isDetailsLoading) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        val userLatLng = if (userLat != null && userLon != null) GeoPoint(userLat, userLon) else null
                        val placeLatLng = GeoPoint((placeDetails ?: selectedPlace)?.latitude ?: 0.0, (placeDetails ?: selectedPlace)?.longitude ?: 0.0)
                        val distance = if (userLatLng != null) {
                            val results = FloatArray(1)
                            android.location.Location.distanceBetween(
                                userLatLng.latitude, userLatLng.longitude,
                                placeLatLng.latitude, placeLatLng.longitude,
                                results
                            )
                            results[0] / 1000.0 // in km
                        } else null
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            // Map at the top in a Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                AndroidView(
                                    factory = { ctx ->
                                        org.osmdroid.views.MapView(ctx).apply {
                                            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                                            setMultiTouchControls(true)
                                            overlays.clear()
                                            // User marker
                                            userLatLng?.let {
                                                val userMarker = Marker(this)
                                                userMarker.position = it
                                                userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                                userMarker.title = "You"
                                                userMarker.icon = null // Use default
                                                overlays.add(userMarker)
                                            }
                                            // Place marker
                                            val placeMarker = Marker(this)
                                            placeMarker.position = placeLatLng
                                            placeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                            placeMarker.title = placeDetails?.name ?: selectedPlace?.name ?: "Place"
                                            overlays.add(placeMarker)
                                            // Polyline between user and place
                                            if (userLatLng != null) {
                                                val line = Polyline()
                                                line.setPoints(listOf(userLatLng, placeLatLng))
                                                overlays.add(line)
                                            }
                                            // Center map
                                            controller.setZoom(14.0)
                                            controller.setCenter(placeLatLng)
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            // Add image carousel here
                            PixabayImageCarousel(
                                query = (placeDetails?.name ?: selectedPlace?.name ?: ""),
                                apiKey = pixabayApiKey,
                                pixabayService = pixabayService
                            )
                            Spacer(Modifier.height(12.dp))
                            if (distance != null) {
                                Text("Distance: %.2f km".format(distance), fontWeight = FontWeight.SemiBold)
                            }
                            Text("Address: ${placeDetails?.address ?: selectedPlace?.address ?: "-"}")
                            Text("Category: ${placeDetails?.category?.displayName ?: selectedPlace?.category?.displayName ?: "-"}")
                            Text("Latitude: ${(placeDetails ?: selectedPlace)?.latitude}")
                            Text("Longitude: ${(placeDetails ?: selectedPlace)?.longitude}")
                            Spacer(Modifier.height(16.dp))
                            // Open in Maps button
                            Button(
                                onClick = {
                                    val uri = if (userLatLng != null) {
                                        Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${userLatLng.latitude},${userLatLng.longitude}&destination=${placeLatLng.latitude},${placeLatLng.longitude}")
                                    } else {
                                        Uri.parse("geo:${placeLatLng.latitude},${placeLatLng.longitude}?q=${Uri.encode(placeDetails?.name ?: selectedPlace?.name ?: "Place")}")
                                    }
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    intent.setPackage("com.google.android.apps.maps")
                                    try { context.startActivity(intent) } catch (_: Exception) {}
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                            ) {
                                Text("Open in Maps", color = Color.White)
                            }
                        }
                    }
                }
            )
        }
    }
}
