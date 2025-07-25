package com.example.touristguide.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.BookmarksViewModel
import com.example.touristguide.ui.screen.BookmarkType
import com.example.touristguide.ui.screen.BookmarkedItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BudgetScreen(navController: NavController) {
    var days by remember { mutableStateOf("") }
    var people by remember { mutableStateOf("") }
    var estimatedCost by remember { mutableStateOf<Int?>(null) }
    var selectedPlace by remember { mutableStateOf("") }

    val bookmarksViewModel: BookmarksViewModel = viewModel()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Budget Planner",
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Text(
                "✨ Plan Your Trip Effortlessly",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = days,
                        onValueChange = { days = it },
                        label = { Text("Trip Duration (Days)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = people,
                        onValueChange = { people = it },
                        label = { Text("Number of Travelers") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Text("🌍 Select Destination Region", style = MaterialTheme.typography.titleMedium)

            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Pokhara", "Kathmandu", "Mustang").forEach { place ->
                    AssistChip(
                        onClick = { selectedPlace = place },
                        label = { Text(place) },
                        shape = RoundedCornerShape(10.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedPlace == place) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = if (selectedPlace == place) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }

            Button(
                onClick = {
                    val d = days.toIntOrNull() ?: 0
                    val p = people.toIntOrNull() ?: 0
                    val (foodPerDay, stayPerDay, transportPerDay) = when (selectedPlace) {
                        "Kathmandu" -> Triple(400, 900, 1800)
                        "Pokhara" -> Triple(500, 1000, 2200)
                        "Mustang" -> Triple(700, 1800, 3500)
                        else -> Triple(533, 1233, 2500) // Average price if no place selected
                    }
                    estimatedCost = if (d > 0 && p > 0) (foodPerDay + stayPerDay + transportPerDay) * d * p else null
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("\uD83D\uDCB0 Estimate Cost", color = Color.White)
            }

            HorizontalDivider(thickness = 1.dp)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("\uD83D\uDCE6 Estimated Costs", style = MaterialTheme.typography.titleMedium)
                    if (estimatedCost != null) {
                        Text("Total Estimated Cost: Rs. ${estimatedCost}", style = MaterialTheme.typography.bodyLarge)
                    } else {
                        Text("Enter trip duration and number of travelers, then press Estimate Cost.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (estimatedCost != null) {
                            val name = if (selectedPlace.isNotEmpty()) selectedPlace else "Custom Trip"
                            val details = "Days: $days, Travelers: $people, Cost: Rs. $estimatedCost"
                            bookmarksViewModel.addBookmark(
                                BookmarkedItem(
                                    id = "", // id will be generated in ViewModel
                                    type = BookmarkType.BUDGET,
                                    name = name,
                                    details = details
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("\uD83D\uDCBE Save Estimate", color = Color.White)
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun BudgetScreenPreview() {
    TouristGuideTheme {
        val fakeNavController = rememberNavController()
        BudgetScreen(navController = fakeNavController)
    }
}