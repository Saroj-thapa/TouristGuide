package com.example.touristguide.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.touristguide.data.model.demoTreks
import com.example.touristguide.data.model.Trekking
import androidx.navigation.NavController
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
import androidx.compose.ui.text.font.FontWeight
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.util.GeoPoint
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.IconButton
import androidx.compose.foundation.Image as ComposeImage
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.TrekkingViewModel
import com.example.touristguide.viewmodel.TrekkingViewModelFactory
import com.example.touristguide.data.repository.TrekkingRepository
import com.example.touristguide.data.network.GeoapifyApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.runtime.collectAsState
import com.example.touristguide.viewmodel.LocationViewModel
import androidx.compose.ui.platform.LocalContext
import org.osmdroid.config.Configuration
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.background
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.touristguide.data.network.PixabayService
import com.example.touristguide.ui.components.PixabayImageCarousel
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrekkingScreen(
    navController: NavController,
    locationViewModel: LocationViewModel = viewModel()
) {
    // --- Setup Geoapify API ---
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://api.geoapify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val geoapifyService = remember { retrofit.create(GeoapifyApiService::class.java) }
    val apiKey = "6acbf75b57b74b749fd87b61351b7c77" // Use your actual API key
    val repository = remember { TrekkingRepository(geoapifyService, apiKey) }
    val factory = remember { TrekkingViewModelFactory(repository) }
    val trekkingViewModel: TrekkingViewModel = viewModel(factory = factory)

    // --- OSMDroid config ---
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    val userLat by locationViewModel.latitude.collectAsState()
    val userLon by locationViewModel.longitude.collectAsState()
    val lat = userLat ?: 27.7172
    val lon = userLon ?: 85.3240

    // --- Fetch all treks in Nepal on first load ---
    LaunchedEffect(Unit) {
        trekkingViewModel.updateNearbyTreks(lat, lon)
    }

    // Fetch all Nepal treks on first load
    LaunchedEffect(Unit) {
        trekkingViewModel.fetchAllNepalTreks(repository)
    }

    val trekList = trekkingViewModel.treks.collectAsState().value
    val isLoading = trekkingViewModel.isLoading.collectAsState().value
    var selectedTrek by remember { mutableStateOf<Trekking?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
    var selectedDuration by remember { mutableStateOf<String?>(null) }
    var selectedSeason by remember { mutableStateOf<String?>(null) }
    var bookmarkedTreks by rememberSaveable { mutableStateOf<Set<String>>(setOf()) }

    // Live search effect (manual debounce, no snapshotFlow)
    LaunchedEffect(searchQuery) {
        kotlinx.coroutines.delay(500)
        if (searchQuery.isNotBlank()) {
            trekkingViewModel.searchTreks(searchQuery, lat, lon)
        } else {
            trekkingViewModel.updateNearbyTreks(lat, lon)
        }
    }

    // For filter chips (use all available treks for options)
    val allTreks = if (trekList.isNotEmpty()) trekList else demoTreks
    val difficulties = allTreks.map { it.difficulty }.distinct()
    val durations = allTreks.map { it.estimatedDuration }.distinct()
    val seasons = allTreks.map { it.bestSeason }.distinct()

    // Filtered trek list (remove searchQuery filter, let API handle it)
    val filteredTreks = (if (trekList.isNotEmpty()) trekList else demoTreks).filter { trek ->
        (selectedDifficulty == null || trek.difficulty == selectedDifficulty) &&
        (selectedDuration == null || trek.estimatedDuration == selectedDuration) &&
        (selectedSeason == null || trek.bestSeason == selectedSeason)
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Trekking Routes",
                navController = navController
            )
        },
        bottomBar = {
            CommonBottomBar(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search bar in a Card (moved to top)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search treks...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Spacer(Modifier.height(8.dp))
            // Map at the top (all treks) in a Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(Modifier.height(220.dp).fillMaxWidth()) {
                    val mapTreks = if (trekList.isNotEmpty()) trekList else demoTreks
                    AndroidView(factory = { context ->
                        val mapView = MapView(context)
                        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                        mapView.setMultiTouchControls(false)
                        val allPoints = mapTreks.flatMap { trek -> trek.stops.map { GeoPoint(it.lat, it.lon) } }
                        if (allPoints.isNotEmpty()) {
                            mapView.controller.setZoom(7.0)
                            mapView.controller.setCenter(allPoints.first())
                        }
                        mapTreks.forEach { trek ->
                            val stopPoints = trek.stops.map { GeoPoint(it.lat, it.lon) }
                            if (stopPoints.size > 1) {
                                val polyline = Polyline().apply {
                                    setPoints(stopPoints)
                                    outlinePaint.color = android.graphics.Color.BLUE
                                    outlinePaint.strokeWidth = 4f
                                }
                                mapView.overlays.add(polyline)
                            }
                            trek.stops.forEach { stop ->
                                val marker = Marker(mapView)
                                marker.position = GeoPoint(stop.lat, stop.lon)
                                marker.title = stop.name
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                mapView.overlays.add(marker)
                            }
                        }
                        mapView
                    }, modifier = Modifier.fillMaxSize())
                }
            }
            Spacer(Modifier.height(8.dp))
            // Filter chips in a Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Difficulty:", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.CenterVertically))
                    Spacer(Modifier.width(4.dp))
                    difficulties.forEach { diff ->
                        AssistChip(
                            onClick = { selectedDifficulty = if (selectedDifficulty == diff) null else diff },
                            label = { Text(diff) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedDifficulty == diff) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (selectedDifficulty == diff) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Duration:", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.CenterVertically))
                    Spacer(Modifier.width(4.dp))
                    durations.forEach { dur ->
                        AssistChip(
                            onClick = { selectedDuration = if (selectedDuration == dur) null else dur },
                            label = { Text(dur) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedDuration == dur) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (selectedDuration == dur) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Season:", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.CenterVertically))
                    Spacer(Modifier.width(4.dp))
                    seasons.forEach { season ->
                        AssistChip(
                            onClick = { selectedSeason = if (selectedSeason == season) null else season },
                            label = { Text(season) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedSeason == season) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (selectedSeason == season) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            // Loading indicator
            if (isLoading) {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                items(filteredTreks) { trek ->
                    TrekCard(
                        trek = trek,
                        isBookmarked = trek.id in bookmarkedTreks,
                        onBookmarkClick = {
                            bookmarkedTreks = if (trek.id in bookmarkedTreks)
                                bookmarkedTreks - trek.id else bookmarkedTreks + trek.id
                        },
                        onClick = { selectedTrek = trek }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
            if (selectedTrek != null) {
                TrekDetailsDialog(
                    trek = selectedTrek!!,
                    isBookmarked = selectedTrek!!.id in bookmarkedTreks,
                    onBookmarkClick = {
                        val id = selectedTrek!!.id
                        bookmarkedTreks = if (id in bookmarkedTreks)
                            bookmarkedTreks - id else bookmarkedTreks + id
                    },
                    onDismiss = { selectedTrek = null }
                )
            }
        }
    }
}

@Composable
fun TrekCard(
    trek: Trekking,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            if (trek.images.firstOrNull() != null) {
                AsyncImage(
                    model = trek.images.firstOrNull(),
                    contentDescription = trek.name,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(null),
                    contentDescription = trek.name,
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(Modifier.width(18.dp))
            Column(Modifier.weight(1f)) {
                Text(trek.name.orEmpty(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Distance: ${trek.distanceKm} km", style = MaterialTheme.typography.bodyMedium)
                Text("Duration: ${trek.estimatedDuration}", style = MaterialTheme.typography.bodyMedium)
                Text("Difficulty: ${trek.difficulty}", style = MaterialTheme.typography.bodyMedium)
                Text("Best Season: ${trek.bestSeason}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onBookmarkClick) {
                if (isBookmarked) {
                    Icon(Icons.Filled.Bookmark, contentDescription = "Bookmarked", tint = MaterialTheme.colorScheme.primary)
                } else {
                    Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Bookmark")
                }
            }
        }
    }
}

@Composable
fun TrekDetailsDialog(
    trek: Trekking,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onDismiss: () -> Unit
) {
    // Pixabay dependencies
    val pixabayApiKey = "51346711-84695fc7acb617448e6e8b140"
    val pixabayRetrofit = remember {
        retrofit2.Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
    }
    val pixabayService = remember { pixabayRetrofit.create(PixabayService::class.java) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 8.dp) {
            Column(Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                // Map at the top for this trek in a Card
                if (trek.stops.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Box(Modifier.height(180.dp).fillMaxWidth()) {
                            AndroidView(factory = { context ->
                                val mapView = MapView(context)
                                mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                                mapView.setMultiTouchControls(false)
                                val geoPoints = trek.stops.map { GeoPoint(it.lat, it.lon) }
                                // Center map
                                if (geoPoints.isNotEmpty()) {
                                    mapView.controller.setZoom(11.0)
                                    mapView.controller.setCenter(geoPoints.first())
                                }
                                // Polyline for route
                                if (geoPoints.size > 1) {
                                    val polyline = Polyline().apply {
                                        setPoints(geoPoints)
                                    }
                                    mapView.overlays.add(polyline)
                                }
                                // Markers for stops
                                trek.stops.forEach { stop ->
                                    val marker = Marker(mapView)
                                    marker.position = GeoPoint(stop.lat, stop.lon)
                                    marker.title = stop.name
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    mapView.overlays.add(marker)
                                }
                                mapView
                            }, modifier = Modifier.fillMaxSize())
                        }
                    }
                }
                // Add image carousel here
                PixabayImageCarousel(
                    query = trek.name ?: "",
                    apiKey = pixabayApiKey,
                    pixabayService = pixabayService
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(trek.name.orEmpty(), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                    IconButton(onClick = onBookmarkClick) {
                        if (isBookmarked) {
                            Icon(Icons.Filled.Bookmark, contentDescription = "Bookmarked", tint = MaterialTheme.colorScheme.primary)
                        } else {
                            Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Bookmark")
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(trek.description.orEmpty())
                Spacer(Modifier.height(8.dp))
                Text("Distance: ${trek.distanceKm} km")
                Text("Duration: ${trek.estimatedDuration}")
                Text("Difficulty: ${trek.difficulty}")
                Text("Altitude: ${trek.minAltitude}–${trek.maxAltitude}m")
                Text("Best Season: ${trek.bestSeason}")
                Spacer(Modifier.height(12.dp))
                WeatherSection(trek)
                Spacer(Modifier.height(8.dp))
                Text("Stops:")
                trek.stops.forEach { stop ->
                    Text("- ${stop.name} (${stop.type})")
                }
                Spacer(Modifier.height(8.dp))
                Text("Tips:")
                trek.tips.forEach { tip ->
                    Text("- $tip")
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun WeatherSection(trek: Trekking) {
    val forecast = trek.weatherForecast
    Column(Modifier.fillMaxWidth()) {
        Text("Weather Forecast", style = MaterialTheme.typography.titleMedium)
        if (forecast.isNotEmpty()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                forecast.take(3).forEach { day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(day.date.orEmpty(), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                        if (day.iconUrl.orEmpty() != null) {
                            ComposeImage(
                                painter = rememberAsyncImagePainter(day.iconUrl.orEmpty()),
                                contentDescription = day.summary.orEmpty(),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(day.summary.orEmpty(), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                        Text("${day.minTemp}°C / ${day.maxTemp}°C", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            Text("No weather data available.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
