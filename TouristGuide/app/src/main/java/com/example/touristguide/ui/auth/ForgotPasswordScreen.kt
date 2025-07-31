package com.example.touristguide.ui.auth

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.touristguide.R
import com.example.touristguide.viewmodel.AuthViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape

@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        authState?.let { result ->
            if (result.isSuccess) {
                Toast.makeText(context, "Reset link sent", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, result.exceptionOrNull()?.message ?: "Reset failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AuthScreenContent(title = "Forgot Password?") {
        Text("Enter your email to receive a reset link.", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.resetPassword(email) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Send Reset Link")
        }
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Back to Login", color = MaterialTheme.colorScheme.primary)
        }
    }
}
