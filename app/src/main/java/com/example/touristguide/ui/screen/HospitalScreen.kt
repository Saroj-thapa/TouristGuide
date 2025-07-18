package com.example.touristguide.ui.screen

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.touristguide.data.model.Place
import com.example.touristguide.ui.components.CommonBottomBar
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.PlaceMap
import com.example.touristguide.utils.Constants
import com.example.touristguide.viewmodel.HospitalViewModel
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.util.GeoPoint
import com.example.touristguide.ui.components.PlaceDetailsDialog
import androidx.compose.material3.MenuAnchorType
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalScreen(navController: NavController, viewModel: HospitalViewModel = viewModel()) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val regions by viewModel.regions.collectAsState()
    val hospitals by viewModel.filteredHospitals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val userLat = Constants.NEPAL_LAT
    val userLon = Constants.NEPAL_LON
    var selectedHospital by remember { mutableStateOf<Place?>(null) }
    LaunchedEffect(Unit) {
        viewModel.fetchHospitalLocation(userLat, userLon)
        viewModel.loadBookmarks()
    }
    Scaffold(
        topBar = { CommonTopBar(title = "Hospitals & Medical", navController = navController) },
        bottomBar = { CommonBottomBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:102"))
                    context.startActivity(intent)
                },
                containerColor = Color(0xFFD32F2F),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call Emergency (102)")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(12.dp)
        ) {
            // --- Search & Filter Section ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Search & Filter", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        label = { Text("Search hospitals by name or address") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    if (regions.isNotEmpty()) {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedRegion,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Filter by Region/District") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All Regions") },
                                    onClick = {
                                        viewModel.setSelectedRegion("")
                                        expanded = false
                                    }
                                )
                                regions.forEach { region ->
                                    DropdownMenuItem(
                                        text = { Text(region) },
                                        onClick = {
                                            viewModel.setSelectedRegion(region)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            // --- Map Section ---
            Text("Map View", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(Modifier.fillMaxSize()) {
                    PlaceMap(
                        context = context,
                        places = hospitals,
                        currentLocation = GeoPoint(userLat, userLon)
                    )
                    // Optional: Show All on Map button (future)
                }
            }
            Spacer(Modifier.height(20.dp))
            // --- Hospital List Section ---
            Text("Nearby Hospitals", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
            if (isLoading) {
                Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (hospitals.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                        Text("No hospitals found.", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    hospitals.forEachIndexed { idx, hospital ->
                        HospitalCard(
                            hospital = hospital,
                            isBookmarked = hospital.name?.let { bookmarks.contains(it) } == true,
                            onBookmark = { viewModel.bookmarkHospital(hospital) },
                            onRemoveBookmark = { viewModel.removeBookmark(hospital) },
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + (hospital.address?.takeIf { it.any { c -> c.isDigit() } } ?: "102")))
                                context.startActivity(intent)
                            },
                            onClick = { selectedHospital = hospital }
                        )
                        if (idx < hospitals.lastIndex) {
                            HorizontalDivider(Modifier.padding(vertical = 2.dp), color = Color(0x11000000))
                        }
                    }
                }
            }
            // Dialog for hospital details
            if (selectedHospital != null) {
                HospitalDetailsDialog(
                    place = selectedHospital!!,
                    onDismiss = { selectedHospital = null }
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun HospitalCard(
    hospital: Place,
    isBookmarked: Boolean,
    onBookmark: () -> Unit,
    onRemoveBookmark: () -> Unit,
    onCall: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (hospital.imageUrl != null) {
                    AsyncImage(
                        model = hospital.imageUrl,
                        contentDescription = hospital.name,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(32.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(hospital.name ?: "", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFB3E5FC),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text("Hospital", fontSize = 11.sp, color = Color(0xFF0277BD), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Text(hospital.address ?: "", fontSize = 13.sp, color = Color.Gray, maxLines = 2)
                Text("Rating: ${hospital.rating?.toString() ?: "N/A"}", fontSize = 12.sp, color = Color.Gray)
                Text("Price: ${hospital.price?.toString() ?: "N/A"}", fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onCall) {
                    Icon(Icons.Default.Call, contentDescription = "Call Hospital", tint = Color(0xFF388E3C))
                }
                IconButton(onClick = { if (isBookmarked) onRemoveBookmark() else onBookmark() }) {
                    if (isBookmarked) {
                        Icon(Icons.Default.Favorite, contentDescription = "Bookmarked", tint = Color.Red)
                    } else {
                        Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Bookmark")
                    }
                }
            }
        }
    }
}

@Composable
fun HospitalDetailsDialog(place: Place, onDismiss: () -> Unit) {
    val context = LocalContext.current
    fun openInMap() {
        val uri = Uri.parse("geo:${place.latitude},${place.longitude}?q=${Uri.encode(place.name)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        context.startActivity(intent)
    }
    fun callHospital() {
        val phone = place.address?.takeIf { it.any { c -> c.isDigit() } } ?: "102"
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(intent)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(place.name ?: "") },
        text = {
            Column(Modifier.width(300.dp)) {
                // Map in Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    PlaceMap(
                        context = context,
                        places = listOf(place),
                        currentLocation = org.osmdroid.util.GeoPoint(place.latitude, place.longitude)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Details
                Text(place.address ?: "")
                Text("Category: ${place.category.displayName}")
                Spacer(modifier = Modifier.height(8.dp))
                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { openInMap() }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Place, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Open in Map")
                    }
                    Button(onClick = { callHospital() }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Call, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Call")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}
