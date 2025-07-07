package com.example.touristguide.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
private data class Hotel(
    val id: String,
    val name: String,
    val location: String,
    val price: Int,
    val rating: Int,
    val isFavorite: Boolean = false
)

private val locations = listOf("Pokhara", "Kathmandu", "Chitwan", "Lumbini")
private val priceRanges = listOf("$ - $$", "$$ - $$$", "$$$ - $$$$", "$$$$ - $$$$$")
private val ratings = listOf(1, 2, 3, 4, 5)

private val hotels = listOf(
    Hotel("1", "Thamel Guest House", "Thamel, Kathmandu", 1500, 4, true),
    Hotel("2", "Lake View Resort", "Pokhara", 2000, 5, true),
    Hotel("3", "Jungle Lodge", "Chitwan", 1800, 4, true),
    Hotel("4", "Mountain Retreat", "Nagarkot", 2500, 5, true)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<String?>(null) }
    var selectedPrice by remember { mutableStateOf<String?>(null) }
    var selectedRating by remember { mutableStateOf<Int?>(null) }

    val filteredHotels = hotels.filter {
        (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
                || it.location.contains(searchQuery, ignoreCase = true)) &&
                (selectedLocation == null || it.location.contains(selectedLocation!!, ignoreCase = true)) &&
                (selectedRating == null || it.rating == selectedRating)
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Hotels & Lodges",
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
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 8.dp)
            ) {
                // Search
                Text("Search", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 2.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search for hotels...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
                Text("Search by hotel name or location", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(start = 20.dp, bottom = 8.dp))

                // Filter by location
                Text("Filter by location", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 2.dp))
                Row(Modifier.padding(horizontal = 12.dp)) {
                    locations.forEach { loc ->
                        FilterChip(
                            selected = selectedLocation == loc,
                            onClick = { selectedLocation = if (selectedLocation == loc) null else loc },
                            label = { Text(loc) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                if (selectedLocation == null) {
                    Text("Select a location", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(start = 20.dp, bottom = 8.dp))
                }
                // Filter by price
                Text("Filter by price range", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 2.dp))
                Row(Modifier.padding(horizontal = 12.dp)) {
                    priceRanges.forEach { price ->
                        FilterChip(
                            selected = selectedPrice == price,
                            onClick = { selectedPrice = if (selectedPrice == price) null else price },
                            label = { Text(price) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                Text("Select a price range", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(start = 20.dp, bottom = 8.dp))

                // Filter by rating
                Text("Filter by rating", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 2.dp))
                Row(Modifier.padding(horizontal = 12.dp)) {
                    ratings.forEach { rate ->
                        FilterChip(
                            selected = selectedRating == rate,
                            onClick = { selectedRating = if (selectedRating == rate) null else rate },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (selectedRating == rate) Color(0xFFFFC107) else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "$rate Star${if (rate > 1) "s" else ""}",
                                        fontWeight = if (selectedRating == rate) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedRating == rate) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFF8E1),
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                containerColor = Color(0xFFF5F5F5),
                                labelColor = Color.Gray
                            ),
                            modifier = Modifier.padding(end = 8.dp, top = 4.dp, bottom = 4.dp)
                        )
                    }
                }
                Text("Select a rating", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(start = 20.dp, bottom = 8.dp))

                // Hotel Cards header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hotel Cards", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    OutlinedButton(onClick = { /* TODO: View more */ }, shape = RoundedCornerShape(12.dp)) {
                        Text("View More")
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
            // Hotel list (remains scrollable independently)
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredHotels) { hotel ->
                    HotelCard(hotel = hotel, onClick = {
                        navController.navigate("hotelDetails/${hotel.id}")
                    })
                }
            }
        }
    }
}

@Composable
private fun HotelCard(hotel: Hotel, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hotel image placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = android.R.drawable.ic_menu_myplaces), contentDescription = null, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(hotel.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(hotel.location, color = Color.Gray, fontSize = 14.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Rs. ${hotel.price} /", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("night", fontSize = 13.sp)
                if (hotel.isFavorite) Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp).padding(start = 2.dp))
            }
        }
    }
    HorizontalDivider(modifier = Modifier.padding(start = 68.dp, end = 8.dp))
}

@Preview(showBackground = true)
@Composable
fun HotelsScreenPreview() {
    TouristGuideTheme {
        val fakeNavController = rememberNavController()
        HotelsScreen(navController = fakeNavController)
    }
}