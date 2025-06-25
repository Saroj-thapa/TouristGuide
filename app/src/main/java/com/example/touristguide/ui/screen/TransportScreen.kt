package com.example.touristguide.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(navController: NavController) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text("Find local transportation options.")
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