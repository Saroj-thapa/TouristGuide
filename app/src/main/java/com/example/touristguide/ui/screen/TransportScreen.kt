package com.example.touristguide.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.touristguide.viewmodel.TransportViewModel

// Data class for transport info
data class TransportInfo(val route: String, val duration: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(navController: NavController, transportViewModel: TransportViewModel = viewModel()) {
    val transports by transportViewModel.transports.collectAsState()

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
                .verticalScroll(rememberScrollState())
                .padding(innerpadding)
        ) {
            Text("Estimated Durations", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            if (transports.isEmpty()) {
                Text("No transport routes available.", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                transports.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.Red, RoundedCornerShape(12.dp))
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(it.route, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(it.duration, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
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