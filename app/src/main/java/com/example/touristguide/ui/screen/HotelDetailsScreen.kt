package com.example.touristguide.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.HotTub
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(hotelId: String, navController: NavController) {
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
                title = { Text(hotel.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color(0xFFF8FAFB)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFFBBDEFB), Color(0xFFE3F2FD)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painter = painterResource(id = android.R.drawable.ic_menu_myplaces), contentDescription = null, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(hotel.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(hotel.location, fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(hotel.description, fontSize = 15.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Facilities", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                hotel.facilities.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(it.icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(it.name, fontSize = 15.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Contact Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(hotel.phone, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(hotel.website, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { /* TODO */ },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Book Now")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { /* TODO */ },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add to Itinerary")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE0F7FA)),
                contentAlignment = Alignment.Center
            ) {
                Text("Map Preview Here", color = Color.Gray)
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
    HotelDetailsScreen(hotelId = "1", navController = rememberNavController())
}
