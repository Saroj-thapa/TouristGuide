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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrekkingScreen(navController: NavController) {
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


            RouteCard(
                title = "Everest Base Camp Trek",
                duration = "12 days",
                difficulty = "Hard"
            )
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