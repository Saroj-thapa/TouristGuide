package com.example.touristguide.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

fun saveRememberMe(context: Context, value: Boolean, email: String? = null) {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("remember_me", value)
        .apply {
            if (value && email != null) {
                putString("remembered_email", email)
            }
        }
        .apply()
}

fun getRememberedEmail(context: Context): String? {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    return prefs.getString("remembered_email", "")
}

fun isRememberMeEnabled(context: Context): Boolean {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("remember_me", false)
}

fun isLoggedIn(context: Context): Boolean {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("is_logged_in", false)
}

fun isLoggedOut(context: Context): Boolean {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("logged_out", false)
}

fun clearLoggedOut(context: Context) {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("logged_out", false).apply()
}

@Composable
fun AuthScreenContent(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        //Optional: Background image
        Image(
            painter = painterResource(id = R.drawable.login_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )


        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_explorenepal),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .shadow(12.dp, CircleShape)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
            )
        }

        // Card with form
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                content()
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisibility by remember {
        mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()
    val isAnonymous by viewModel.isAnonymous.collectAsState()
    val navController = navController

    // Google Sign-In client
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // Launcher for Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.firebaseAuthWithGoogle(idToken) { success, error ->
                    if (success) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, error ?: "Google sign-in failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Check if user is logged out and force login
    LaunchedEffect(Unit) {
        if (isLoggedOut(context)) {
            // Stay on login screen, do not auto-navigate
        } else if (isRememberMeEnabled(context) && isLoggedIn(context)) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    LaunchedEffect(authState) {
        authState?.let { result ->
            if (result.isSuccess) {
                clearLoggedOut(context)
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            } else {
                Toast.makeText(context, result.exceptionOrNull()?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(isAnonymous) {
        if (isAnonymous) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    // Autofill email if Remember Me is enabled
    LaunchedEffect(Unit) {
        if (isRememberMeEnabled(context)) {
            rememberMe = true
            email = getRememberedEmail(context) ?: ""
        }
    }

    AuthScreenContent(title = "ExploreNepal") {
        OutlinedTextField(value = email, onValueChange = { email = it },
            label = { Text("Email")
                },
            prefix = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
//                        label = { Text("Password") },
            prefix = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            suffix = {
                Icon(
                    painter = painterResource(if (passwordVisibility)
                        R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        passwordVisibility = !passwordVisibility
                    }
                )
            },
            shape = RoundedCornerShape(12.dp),
            visualTransformation =if (passwordVisibility)
                VisualTransformation.None else PasswordVisualTransformation(),
            placeholder = {
                Text("*******")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth(),

            )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                Text("Remember Me")
            }
            Text(
                "Forgot Password?",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                saveRememberMe(context, rememberMe, if (rememberMe) email else null)
                viewModel.login(email, password)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Login")
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { viewModel.loginAnonymously() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Continue as Guest")
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.Center
        ){ Spacer(modifier = Modifier.width(15.dp))
            Image(
                painter = painterResource(R.drawable.googlelogo),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    }
            )
        }
        Spacer(Modifier.height(16.dp))
        Text("Don't have an account? Sign Up", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable {
            navController.navigate(Routes.SIGNUP)
        })
    }
}
