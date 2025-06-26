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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
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
            var searchQuery by remember { mutableStateOf("") }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search hospitals, departments...") },
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
                    Text("City Hospital", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Kathmandu, Nepal", fontSize = 14.sp, color = Color.Gray)
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
                Text("Image of the Hospital")
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HospitalRatingCard(
                    user = "John123",
                    review = "Excellent staff and facilities!",
                    rating = "★★★★★"
                )
                HospitalRatingCard(
                    user = "Sarah456",
                    review = "Clean and modern hospital.",
                    rating = "★★★★☆"
                )
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Location (Map Placeholder)")
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






