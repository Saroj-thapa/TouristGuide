package com.example.touristguide.ui.profile
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.navigation.Routes
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
import com.example.touristguide.ui.home.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.AuthViewModel
import com.example.touristguide.viewmodel.ProfileViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import coil.compose.rememberAsyncImagePainter
import com.example.touristguide.data.model.User
import android.util.Log
import com.example.touristguide.ui.screen.BookMarkScreen
import com.example.touristguide.ui.screen.BookmarkedItem
import com.example.touristguide.ui.screen.BookmarkType
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.util.HashMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val user by authViewModel.user.collectAsState()
    val userName by authViewModel.userName.collectAsState("")
    val userEmail by authViewModel.userEmail.collectAsState("")
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var passwordMessage by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    val isAnonymous by authViewModel.isAnonymous.collectAsState()
    var showPasswordUpdatedToast by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    val context = LocalContext.current
    // Cloudinary config (do this once, e.g. in Application class, but for demo here)
    try {
        val config: HashMap<String, String> = HashMap()
        config["cloud_name"] = "dkeyvjzar"
        config["api_key"] = "376497286791748"
        config["api_secret"] = "I8fceZjN-e02NFkptOi8bfzsFRM"
        MediaManager.init(context, config)
    } catch (e: Exception) {
        // Already initialized, ignore
    }

    LaunchedEffect(Unit) { authViewModel.fetchUser() }
    LaunchedEffect(user, userName, userEmail) {
        Log.d("ProfileScreen", "user: $user, userName: $userName, userEmail: $userEmail")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTopBar(
                title = "Profile",
                navController = navController,
                showBackButton = false
            )
        },
        bottomBar = {
            CommonBottomBar(navController = navController)
        },
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Profile Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .clickable {
                                imageLauncher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            profileImageUri != null -> {
                                AsyncImage(
                                    model = profileImageUri,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier.size(80.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            !user?.profileImageUrl.isNullOrBlank() -> {
                                AsyncImage(
                                    model = user?.profileImageUrl,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier.size(80.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (isAnonymous) "Anonymous" else user?.let { "${it.firstName} ${it.lastName}".trim() } ?: userName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        if (!isAnonymous) {
                            Text(
                                text = user?.email ?: userEmail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            if (!isAnonymous) {
                // Account Settings Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = "Account Settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            OutlinedTextField(
                                value = user?.let { "${it.firstName} ${it.lastName}".trim() } ?: userName,
                                onValueChange = { },
                                label = { Text("Username") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = user?.email ?: userEmail,
                                onValueChange = { },
                                label = { Text("Email") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
                // Change Password Button (only for non-anonymous)
                Button(
                    onClick = { showChangePasswordDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Change Password")
                }
            }

            // Preferences Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp)
            ) {
                Text(
                    text = "Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column {
                        SettingItem(
                            icon = Icons.Outlined.Language,
                            title = "Language",
                            subtitle = "English"
                        )
                        HorizontalDivider()
                        SettingItem(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            subtitle = "On"
                        )
                        // Bookmark Button
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { navController.navigate(Routes.BOOKMARKS) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bookmark,
                                contentDescription = "Bookmarks",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Show Bookmarks")
                        }
                        HorizontalDivider()
                        SettingItem(
                            icon = Icons.Default.Info,
                            title = "About App",
                            subtitle = null
                        )
                    }
                }
            }

            // Logout Button
            Button(
                onClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }

    if (showChangePasswordDialog && !isAnonymous) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Change Password") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") }
                    )
                    if (passwordMessage.isNotBlank()) {
                        Text(passwordMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    profileViewModel.changePassword(newPassword) { success, message ->
                        passwordMessage = message
                        if (success) {
                            showChangePasswordDialog = false
                            showPasswordUpdatedToast = true
                        }
                    }
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                Button(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPasswordUpdatedToast) {
        LaunchedEffect(showPasswordUpdatedToast) {
            Toast.makeText(context, "New password is updated", Toast.LENGTH_SHORT).show()
            showPasswordUpdatedToast = false
        }
    }

    // Upload to Cloudinary when image is picked
    if (profileImageUri != null) {
        LaunchedEffect(profileImageUri) {
            try {
                MediaManager.get().upload(profileImageUri).callback(object : com.cloudinary.android.callback.UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val url = resultData?.get("secure_url") as? String
                        if (url != null) {
                            profileViewModel.updateProfileImageUrl(url) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    authViewModel.fetchUser() // Refresh user data
                                    profileImageUri = null // Reset local URI so UI uses saved URL
                                }
                            }
                        }
                    }
                    override fun onError(requestId: String?, error: com.cloudinary.android.callback.ErrorInfo?) {
                        Log.e("ProfileScreen", "Upload failed: ${error?.description}")
                        Toast.makeText(context, "Upload failed: ${error?.description}", Toast.LENGTH_SHORT).show()
                    }
                    override fun onReschedule(requestId: String?, error: com.cloudinary.android.callback.ErrorInfo?) {
                        Log.e("ProfileScreen", "Upload rescheduled: ${error?.description}")
                    }
                }).dispatch()
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Image upload failed", e)
                Toast.makeText(context, "Image upload failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    TouristGuideTheme {
        val navController = rememberNavController()
        ProfileScreen(navController)
    }
}
