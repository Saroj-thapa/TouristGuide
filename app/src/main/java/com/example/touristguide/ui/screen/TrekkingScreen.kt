package com.example.touristguide.ui.screen


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
import com.example.touristguide.viewmodel.FirebaseViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.LocationViewModel
import com.example.touristguide.viewmodel.TrekkingViewModel
import androidx.compose.runtime.collectAsState
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrekkingScreen(
    navController: NavController,
    trekkingViewModel: TrekkingViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel(),
    firebaseViewModel: FirebaseViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
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
    ) { innerpadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerpadding)
        ) {

            Text("Trekking Routes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))


            // Example dynamic trek list (replace placeholder):
            val trekList = trekkingViewModel.treks.collectAsState().value
            if (trekList.isEmpty()) {
                Text("No treks available.", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                trekList.forEach { trek ->
                    RouteCard(title = trek.name, duration = trek.duration, difficulty = trek.difficulty)
                }
            }
            Spacer(Modifier.height(16.dp))
            RouteCard(
                title = "Annapurna Base Camp Trek",
                duration = "10 days",
                difficulty = "Medium"
            )
            Spacer(Modifier.height(24.dp))

            // Action Buttons
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.White),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text("Share", color = Color.Black)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.White),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text("Download Map", color = Color.Black)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.Black),
            ) {
                Text("Save Route", color = Color.White)
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val userPlans = firebaseViewModel.userPlans.collectAsState().value
            val bookmarks = firebaseViewModel.bookmarks.collectAsState().value
            // Example: Add Bookmark and Add to Plan buttons for each trek
            // treks.forEach { trek -> // treks is not defined in this scope
            //     Row {
            //         Button(onClick = {
            //             val bookmarkId = "bookmark_${System.currentTimeMillis()}"
            //             val bookmarkData = mapOf(
            //                 "userId" to userId,
            //                 "name" to trek.name,
            //                 "location" to trek.location,
            //                 "lat" to trek.lat,
            //                 "lon" to trek.lon
            //             )
            //             firebaseViewModel.addBookmark(userId, bookmarkId, bookmarkData)
            //         }) { Text("Bookmark") }
            //         Spacer(Modifier.width(8.dp))
            //         Button(onClick = {
            //             val planId = "plan_${System.currentTimeMillis()}"
            //             val planData = mapOf(
            //                 "userId" to userId,
            //                 "name" to trek.name,
            //                 "location" to trek.location,
            //                 "lat" to trek.lat,
            //                 "lon" to trek.lon
            //             )
            //             firebaseViewModel.savePlan(planId, planData)
            //         }) { Text("Add to Plan") }
            //     }
            // }
            // Display user's bookmarks
            Text("My Bookmarked Treks", fontWeight = FontWeight.Bold)
            if (bookmarks.isEmpty()) {
                Text("No bookmarked treks yet.", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                bookmarks.forEach { bookmark ->
                    val name = bookmark["name"] as? String ?: "Unknown"
                    val location = bookmark["location"] as? String ?: ""
                    Text("$name - $location")
                }
            }
            // Display user's plans
            Text("My Trekking Plans", fontWeight = FontWeight.Bold)
            if (userPlans.isEmpty()) {
                Text("No trekking plans yet.", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                userPlans.forEach { plan ->
                    val name = plan["name"] as? String ?: "Unknown"
                    val location = plan["location"] as? String ?: ""
                    Text("$name - $location")
                }
            }
        }
    }




}
@Composable
fun RouteCard(title: String, duration: String, difficulty: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(title, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
        Text("Duration: $duration, Difficulty: $difficulty",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun TrekkingScreenPreview() {
    TouristGuideTheme {
        val fakeNavController = rememberNavController()
        TrekkingScreen(navController = fakeNavController)
    }
}