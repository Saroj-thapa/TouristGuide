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
fun TrekkingScreen(navController: NavController) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text("Explore trekking routes in Nepal.")
        }
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