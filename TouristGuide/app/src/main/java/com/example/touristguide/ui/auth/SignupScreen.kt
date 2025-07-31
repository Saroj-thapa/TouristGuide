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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.touristguide.R
import com.example.touristguide.navigation.Routes
import com.example.touristguide.viewmodel.AuthViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun SignupScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var passwordVisibility by remember {
        mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        authState?.let { result ->
            if (result.isSuccess) {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SIGNUP) { inclusive = true }
                }
            } else {
                Toast.makeText(context, result.exceptionOrNull()?.message ?: "Signup failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AuthScreenContent(title = "Create Account") {
        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password") }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisibility) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                val icon =
                    if (passwordVisibility) R.drawable.baseline_visibility_24
                    else R.drawable.baseline_visibility_off_24
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = if (passwordVisibility) "Hide password" else "Show password"
                    )
                }
            }
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = confirmPassword, onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisibility) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisibility) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24
                IconButton(onClick = { passwordVisibility= !passwordVisibility }) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = if (passwordVisibility) "Hide password" else "Show password"
                    )
                }
            }

        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (password == confirmPassword && password.isNotEmpty()) {
                    viewModel.signup(email, password, firstName, lastName)
                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Sign Up")
        }
        Spacer(Modifier.height(16.dp))
        Text("Already have an account? Login", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable {
            navController.navigate(Routes.LOGIN)
        })
    }
}
