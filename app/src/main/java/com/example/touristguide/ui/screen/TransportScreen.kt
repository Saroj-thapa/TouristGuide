package com.example.touristguide.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.TransportViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.example.touristguide.ui.components.PlaceMap
import org.osmdroid.util.GeoPoint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.Search
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip

// Data class for transport info
data class TransportInfo(val route: String, val duration: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(navController: NavController, transportViewModel: TransportViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val filterOptions = transportViewModel.filterOptions
    val selectedFilter by transportViewModel.selectedFilter.collectAsState()
    val places by transportViewModel.places.collectAsState()
    val isLoading by transportViewModel.isLoading.collectAsState()
    val error by transportViewModel.error.collectAsState()
    val userLat = 27.7172
    val userLon = 85.3240
    val context = LocalContext.current
    val userGeoPoint = GeoPoint(userLat, userLon)
    val favorites = remember { mutableStateOf(setOf<String>()) }
    val selectedPlace = remember { mutableStateOf<com.example.touristguide.data.model.Place?>(null) }
    val searchText = remember { mutableStateOf("") }

    LaunchedEffect(selectedFilter) {
        transportViewModel.fetchNearbyTransport(userLat, userLon, selectedFilter)
    }

    Scaffold(
        topBar = { CommonTopBar(title = "Transport", navController = navController) },
        bottomBar = { CommonBottomBar(navController = navController) }
    ) { innerpadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerpadding)
        ) {
            // Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    label = { Text("Search transport...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            transportViewModel.searchTransport(userLat, userLon, selectedFilter, searchText.value)
                        }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                    }
                )
                // Removed external search button
            }
            // Map at the top
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                PlaceMap(
                    context = context,
                    places = places,
                    currentLocation = userGeoPoint,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { type ->
                    val icon = when (type) {
                        "Bus" -> Icons.Filled.DirectionsBus
                        "Taxi" -> Icons.Filled.LocalTaxi
                        "Parking" -> Icons.Filled.LocalParking
                        "Rental" -> Icons.Filled.DirectionsCar
                        "Fuel" -> Icons.Filled.LocalGasStation
                        "Airport" -> Icons.Filled.Flight
                        else -> Icons.Filled.DirectionsTransit
                    }
                    AssistChip(
                        onClick = { transportViewModel.updateFilter(type, userLat, userLon) },
                        label = { Text(type) },
                        leadingIcon = { Icon(icon, contentDescription = null) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedFilter == type) Color(0xFF1976D2) else Color(0xFFE3F2FD),
                            labelColor = if (selectedFilter == type) Color.White else Color(0xFF1976D2)
                        ),
                        border = null,
                        modifier = Modifier
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            // Transport Card List
            if (isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
            } else {
                places.forEach { place ->
                    val id = "${place.name}_${place.latitude}_${place.longitude}"
                    val fuelKeywords = listOf("fuel", "gas", "petrol", "diesel", "gas station", "petrol pump", "पेट्रोल", "ग्यास", "पम्प", "पेट्रोल पम्प")
                    val icon = when {
                        place.name?.contains("bus", true) == true -> Icons.Filled.DirectionsBus
                        place.name?.contains("taxi", true) == true -> Icons.Filled.LocalTaxi
                        place.name?.contains("parking", true) == true -> Icons.Filled.LocalParking
                        place.name?.contains("rental", true) == true -> Icons.Filled.DirectionsCar
                        fuelKeywords.any { place.name?.contains(it, ignoreCase = true) == true } -> Icons.Filled.LocalGasStation
                        place.name?.contains("airport", true) == true -> Icons.Filled.Flight
                        else -> Icons.Filled.DirectionsTransit
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { selectedPlace.value = place },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF1976D2), shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (place.imageUrl != null) {
                                    AsyncImage(
                                        model = place.imageUrl,
                                        contentDescription = place.name,
                                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(place.name ?: "", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(place.address ?: "", fontSize = 13.sp, color = Color.Gray)
                            }
                            IconButton(onClick = {
                                favorites.value = if (favorites.value.contains(id)) favorites.value - id else favorites.value + id
                            }) {
                                if (favorites.value.contains(id)) {
                                    Icon(Icons.Filled.Favorite, contentDescription = "Saved", tint = Color(0xFFD32F2F))
                                } else {
                                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Save", tint = Color(0xFFD32F2F))
                                }
                            }
                        }
                    }
                }
            }
            // Dialog for selected place
            if (selectedPlace.value != null) {
                AlertDialog(
                    onDismissRequest = { selectedPlace.value = null },
                    confirmButton = {
                        Row {
                            TextButton(onClick = { selectedPlace.value = null }) {
                                Text("Close")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = {
                                val lat = selectedPlace.value!!.latitude
                                val lon = selectedPlace.value!!.longitude
                                val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lon")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                context.startActivity(mapIntent)
                            }) {
                                Icon(Icons.Filled.Place, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Open in Maps")
                            }
                        }
                    },
                    title = null,
                    text = {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val fuelKeywords = listOf("fuel", "gas", "petrol", "diesel", "gas station", "petrol pump", "पेट्रोल", "ग्यास", "पम्प", "पेट्रोल पम्प")
                                    val icon = when {
                                        selectedPlace.value!!.name?.contains("bus", true) == true -> Icons.Filled.DirectionsBus
                                        selectedPlace.value!!.name?.contains("taxi", true) == true -> Icons.Filled.LocalTaxi
                                        selectedPlace.value!!.name?.contains("parking", true) == true -> Icons.Filled.LocalParking
                                        selectedPlace.value!!.name?.contains("rental", true) == true -> Icons.Filled.DirectionsCar
                                        fuelKeywords.any { selectedPlace.value!!.name?.contains(it, ignoreCase = true) == true } -> Icons.Filled.LocalGasStation
                                        selectedPlace.value!!.name?.contains("airport", true) == true -> Icons.Filled.Flight
                                        else -> Icons.Filled.DirectionsTransit
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFF1976D2), shape = RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(selectedPlace.value!!.name ?: "", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(selectedPlace.value!!.address ?: "", fontSize = 14.sp, color = Color.Gray)
                                Text("Type: ${selectedPlace.value!!.category}", fontSize = 13.sp)
                                Spacer(Modifier.height(12.dp))
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(Modifier.height(180.dp).fillMaxWidth()) {
                                        PlaceMap(
                                            context = context,
                                            places = listOf(selectedPlace.value!!),
                                            currentLocation = userGeoPoint
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransportScreenPreview() {
    TouristGuideTheme {
        val fakeNavController = rememberNavController()
        TransportScreen(navController = fakeNavController)
    }
}