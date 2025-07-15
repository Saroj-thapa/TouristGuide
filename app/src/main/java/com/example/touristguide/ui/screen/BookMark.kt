package com.example.touristguide.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Types of bookmarks
enum class BookmarkType { PLACE, TREKKING_ROUTE, HOTEL }

data class BookmarkedItem(val type: BookmarkType, val name: String, val details: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookMarkScreen(bookmarks: List<BookmarkedItem>, navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            title = { Text("Bookmarks") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        Column(modifier = Modifier.padding(8.dp)) {
            BookmarkType.values().forEach { type ->
                val items = bookmarks.filter { it.type == type }
                if (items.isNotEmpty()) {
                    Text(
                        text = when(type) {
                            BookmarkType.PLACE -> "Places"
                            BookmarkType.TREKKING_ROUTE -> "Trekking Routes"
                            BookmarkType.HOTEL -> "Hotels"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(items) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(item.name, style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(item.details, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
