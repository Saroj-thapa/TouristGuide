package com.example.touristguide.guest

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.touristguide.guest.GuestManager
import com.example.touristguide.navigation.Routes
import com.google.firebase.auth.FirebaseAuth

@Composable
fun requireLoginForAction(
    context: Context,
    navController: NavController,
    onProceed: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val isGuest = FirebaseAuth.getInstance().currentUser == null &&
            GuestManager.getOrCreateGuestId(context).isNotEmpty()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Login Required") },
            text = { Text("You need to log in or sign up to use this feature.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate(Routes.LOGIN)
                }) { Text("Login/Sign Up") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (isGuest) {
        showDialog = true
    } else {
        onProceed()
    }
}

