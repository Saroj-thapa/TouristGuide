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
fun TransportScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Transport",
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
            Text("Major Cities/Places", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            listOf("City A", "City B", "City C").forEach {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.LightGray, RoundedCornerShape(4.dp))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(it, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(16.dp))


            Text("Bus Fare Info", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("City A", fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Bus Image")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Bus\nTypical fare: $5", fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(16.dp))


            Text("Taxi Cost Info", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("City B", fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Taxi Image")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Taxi\nAverage cost: $15", fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(16.dp))


            Text("Estimated Durations", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            listOf(
                "City A to City B" to "Approx. 1 hour",
                "City B to City C" to "Approx. 45 minutes"
            ).forEach {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.Red, RoundedCornerShape(12.dp))
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(it.first, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(it.second, fontSize = 12.sp, color = Color.Gray)
                    }
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
                Text("Bus Stops Map")
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