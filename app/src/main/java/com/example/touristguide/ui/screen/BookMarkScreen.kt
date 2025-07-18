package com.example.touristguide.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.Delete
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.BookmarksViewModel
import androidx.compose.runtime.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.DirectionsBus

// Types of bookmarks
enum class BookmarkType { PLACE, TREKKING_ROUTE, HOTEL, FOOD, HOSPITAL, TRANSPORTATION }

data class BookmarkedItem(val type: BookmarkType, val name: String, val details: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookMarkScreen(bookmarks: List<BookmarkedItem>, navController: NavController) {
    val viewModel: BookmarksViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            Column {
                TopAppBar(
                    title = { Text("Bookmarks", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
                if (bookmarks.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bookmark,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No bookmarks yet!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        BookmarkType.values().forEach { type ->
                            val items = bookmarks.filter { it.type == type }
                            if (items.isNotEmpty()) {
                                item {
                                    Text(
                                        text = when (type) {
                                            BookmarkType.PLACE -> "Places"
                                            BookmarkType.TREKKING_ROUTE -> "Trekking Routes"
                                            BookmarkType.HOTEL -> "Hotels"
                                            BookmarkType.FOOD -> "Food"
                                            BookmarkType.HOSPITAL -> "Hospitals"
                                            BookmarkType.TRANSPORTATION -> "Transportation"
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(items, key = { it.hashCode() }) { item ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        shape = MaterialTheme.shapes.large,
                                        elevation = CardDefaults.cardElevation(4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = when (item.type) {
                                                    BookmarkType.PLACE -> Icons.Filled.Place
                                                    BookmarkType.TREKKING_ROUTE -> Icons.Filled.Terrain
                                                    BookmarkType.HOTEL -> Icons.Filled.Hotel
                                                    BookmarkType.FOOD -> Icons.Filled.Restaurant
                                                    BookmarkType.HOSPITAL -> Icons.Filled.LocalHospital
                                                    BookmarkType.TRANSPORTATION -> Icons.Filled.DirectionsBus
                                                },
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    item.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    item.details,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            IconButton(onClick = {
                                                viewModel.removeBookmark(item)
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Bookmark removed")
                                                }
                                            }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Remove")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}