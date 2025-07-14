package com.example.touristguide.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.HospitalViewModel
import com.example.touristguide.utils.Constants
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalScreen(navController: NavController, viewModel: HospitalViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    val geoapifyData = viewModel.geoapifyData.collectAsState().value
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
            com.google.android.gms.maps.model.LatLng(Constants.NEPAL_LAT, Constants.NEPAL_LON), 12f
        )
    }
    LaunchedEffect(Unit) {
        viewModel.fetchHospitalLocation(Constants.NEPAL_LAT, Constants.NEPAL_LON)
    }
    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Hospitals & Medical",
                navController = navController
            )
        },
        bottomBar = {
            CommonBottomBar(navController = navController)
        }
    ) { innerpadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerpadding)
        ) {
            // Hospital Info Section
            val hospProps = geoapifyData?.features?.firstOrNull()?.properties
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Photo (placeholder)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = hospProps?.name ?: "Unknown Hospital",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = hospProps?.amenity ?: hospProps?.city ?: hospProps?.country ?: "Info not available",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            // Map and Address Section
            Text("Hospital Location in Nepal (Kathmandu)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            val address = hospProps?.formatted
            if (address != null) {
                Text("Address: $address", fontSize = 14.sp, color = Color.DarkGray)
            } else {
                Text("Loading address...", fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = com.google.maps.android.compose.MarkerState(
                            position = com.google.android.gms.maps.model.LatLng(Constants.NEPAL_LAT, Constants.NEPAL_LON)
                        ),
                        title = hospProps?.name ?: "Kathmandu"
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("About the Hospital", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "City Hospital is a multi-specialty hospital offering a range of services including emergency care, outpatient services, and advanced medical treatments.",
                fontSize = 14.sp
            )

            Spacer(Modifier.height(16.dp))
            Text("Patient Reviews", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            // For reviews:
            val reviews = listOf(
                // Replace with dynamic data if available
                "Excellent staff and facilities!" to "John123",
                "Clean and modern hospital." to "Sarah456"
            )
            if (reviews.isEmpty()) {
                Text("No reviews yet.", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    reviews.forEach { (review, user) ->
                        HospitalRatingCard(user = user, review = review, rating = "★★★★★")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { }) {
                    Text("Bookmark")
                }
                Button(onClick = { }, colors = ButtonDefaults.buttonColors(Color.Black)) {
                    Text("Book Appointment", color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Contact Options", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            ContactOption(label = "Call Hospital", description = "+977-123456789")
            ContactOption(label = "Call Ambulance", description = "102 (Emergency)")

            Spacer(Modifier.height(16.dp))


            Text("Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            HospitalInfoBox(title = "Availability", value = "24/7 Service")
            HospitalInfoBox(title = "Emergency Services", value = "Available All Day")
        }
    }

}

@Composable
fun HospitalRatingCard(user: String, review: String, rating: String) {
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
fun ContactOption(label: String, description: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
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
fun HospitalInfoBox(title: String, value: String) {
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
fun HospitalScreenPreview() {
    TouristGuideTheme {
        val fakeNavController = rememberNavController()
        HospitalScreen(navController = fakeNavController)
    }
}
