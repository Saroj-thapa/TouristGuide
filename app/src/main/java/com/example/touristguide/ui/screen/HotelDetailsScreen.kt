package com.example.touristguide.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.HotTub
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(hotelId: String, navController: NavController) {
    // Dummy data for demo
    val hotel = when (hotelId) {
        "1" -> HotelDetails(
            name = "Thamel Guest House",
            location = "Thamel, Kathmandu",
            price = 1500,
            description = "A cozy guest house in the heart of Thamel, perfect for travelers seeking comfort and convenience.",
            facilities = listOf(
                Facility("Wi-Fi", Icons.Default.Wifi),
                Facility("Hot Water", Icons.Default.HotTub),
                Facility("Parking", Icons.Default.LocalParking)
            ),
            phone = "+1234567890",
            website = "www.ecolodge.com"
        )
        "2" -> HotelDetails(
            name = "Lake View Resort",
            location = "Pokhara",
            price = 2000,
            description = "Enjoy breathtaking lake views and luxury amenities at Lake View Resort.",
            facilities = listOf(
                Facility("Wi-Fi", Icons.Default.Wifi),
                Facility("Hot Water", Icons.Default.HotTub),
                Facility("Parking", Icons.Default.LocalParking)
            ),
            phone = "+1234567891",
            website = "www.lakeview.com"
        )
        else -> HotelDetails(
            name = "Eco Lodge",
            location = "Chitwan",
            price = 1800,
            description = "Experience nature and comfort at Eco Lodge, Chitwan.",
            facilities = listOf(
                Facility("Wi-Fi", Icons.Default.Wifi),
                Facility("Hot Water", Icons.Default.HotTub),
                Facility("Parking", Icons.Default.LocalParking)
            ),
            phone = "+1234567892",
            website = "www.ecolodge.com"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hotel Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Call, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Hotel image and name
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painter = painterResource(id = android.R.drawable.ic_menu_myplaces), contentDescription = null, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(hotel.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(hotel.location, color = Color.Gray, fontSize = 15.sp)
                }
            }
            // Description
            Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
            Text(hotel.description, color = Color.Gray, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp))
            // Facilities
            Text("Facilities", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                hotel.facilities.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
                        Icon(it.icon, contentDescription = null, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(it.name, fontSize = 16.sp)
                    }
                    HorizontalDivider()
                }
            }
            // Contact Info
            Text("Contact Info", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(hotel.phone, fontSize = 16.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(hotel.website, fontSize = 16.sp)
            }
            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { /* Book Now */ },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Book Now")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { /* Add to Itinerary */ },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add to Itinerary")
                }
            }
            // Map preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(24.dp)
                    .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Location of the Eco Lodge", color = Color.Gray, fontSize = 16.sp)
            }
        }
    }
}

data class Facility(val name: String, val icon: ImageVector)
data class HotelDetails(
    val name: String,
    val location: String,
    val price: Int,
    val description: String,
    val facilities: List<Facility>,
    val phone: String,
    val website: String
)

@Preview(showBackground = true)
@Composable
fun HotelDetailsScreenPreview() {
    HotelDetailsScreen(hotelId = "1", navController = androidx.navigation.compose.rememberNavController())
}
