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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesScreen(navController: NavController) {
    val scrollState = rememberScrollState()
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


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Images of the Place")
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

            // 6. Community Ratings
            Text("Community Ratings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RatingCard(user = "User123", review = "Great place to visit!", rating = "★★★★★")
                RatingCard(user = "Traveler456", review = "Highly recommend!", rating = "★★★★☆")
            }

            Spacer(Modifier.height(16.dp))

            // 7. Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { }) {
                    Text("Bookmark")
                }
                Button(onClick = { }, colors = ButtonDefaults.buttonColors(Color.Black)) {
                    Text("Add to Plan", color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Preview Location (Map Placeholder)")
            }

            Spacer(Modifier.height(16.dp))


            Text("Transport Options", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            TransportOption(label = "Drive", description = "Approx. 2 hours")
            TransportOption(label = "Public Transport", description = "Bus available")

            Spacer(Modifier.height(16.dp))


            Text("Weather Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoBox(title = "Temperature", value = "25°C")
                InfoBox(title = "Wind Speed", value = "10 km/h")
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
