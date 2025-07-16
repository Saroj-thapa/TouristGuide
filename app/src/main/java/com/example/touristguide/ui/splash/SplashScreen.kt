package com.example.touristguide.ui.splash

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.touristguide.R
import com.example.touristguide.ui.auth.isRememberMeEnabled
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.platform.LocalContext

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(true) {
        Handler(Looper.getMainLooper()).postDelayed({
            val user = auth.currentUser
            val rememberMe = isRememberMeEnabled(context)
            if (user != null && rememberMe) {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }, 2000) // 2 seconds splash delay
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_explorenepal), // replace with your app logo
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("ExploreNepal", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Discover. Travel. Explore.", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}