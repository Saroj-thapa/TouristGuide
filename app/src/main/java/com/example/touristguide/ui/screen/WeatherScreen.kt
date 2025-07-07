package com.example.touristguide.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.graphics.Brush

@Composable
fun WeatherScreen(navController: NavController) {
    var location by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Weather",
                navController = navController
            )
        },
        bottomBar = {
            CommonBottomBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn (
                modifier = Modifier
                    .padding(5.dp)
            ){
                item {

                    OutlinedTextField(//1
                        value = location,
                        onValueChange = { input ->
                            location = input
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        shape = RoundedCornerShape(12.dp),

                        placeholder = {
                            Text(
                                "Search location or auto-locate"
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Gray.copy(0.2f),
                            unfocusedContainerColor = Color.Gray.copy(0.2f),
                        ),
                        suffix = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Enter location or use GPS",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light
                        )
                    )
                    Spacer(modifier = Modifier.padding(vertical = 5.dp))
                    Text(
                        text = "Current Weather",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.LightGray)
                        ) {
                            // Replace with Image(...) to load weather icon
                        }
                        Spacer(modifier = Modifier.width(5.dp))

                        Column {
                            Text(
                                text = "\uD83C\uDF24", // 🌤
                                fontSize = 24.sp,
                                modifier = Modifier.padding(top = 5.dp)
                            )

                            Text("Partly Cloudy")
                        }
                    }
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.1f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Forecast for 7 Days",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Row(//1
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFB2EBF2), Color(0xFF80DEEA))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Weather Icon",
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Day 1",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                            )
                            Text(
                                text = "Monday",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = Color(0xFF616161)
                                )
                            )
                            // "Rain expected" box
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Rain expected",
                                    fontSize = 8.sp,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = "\uD83C\uDF27", // Unicode for 🌧
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 5.dp)
                            )

                        }
                    }
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.1f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Row(//2
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFB2EBF2), Color(0xFF80DEEA))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Weather Icon",
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Day 2",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                            )
                            Text(
                                text = "Tuesday",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = Color(0xFF616161)
                                )
                            )
                            // "Rain expected" box
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Party Cloudy",
                                    fontSize = 8.sp,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = "\uD83C\uDF27", // Unicode for 🌧
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 5.dp)
                            )

                        }
                    }
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.1f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))
                    Row(//3
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFB2EBF2), Color(0xFF80DEEA))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Weather Icon",
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Day 3",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                            )
                            Text(
                                text = "Wednesday",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = Color(0xFF616161)
                                )
                            )
                            // "Rain expected" box
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Mild Weather",
                                    fontSize = 8.sp,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = "\uD83C\uDF27", // Unicode for 🌧
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 5.dp)
                            )

                        }
                    }
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.1f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))
                    Row(//4
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFB2EBF2), Color(0xFF80DEEA))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Weather Icon",
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Day 4",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                            )
                            Text(
                                text = "Thursday",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = Color(0xFF616161)
                                )
                            )
                            // "Rain expected" box
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Sunny",
                                    fontSize = 8.sp,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = "\uD83C\uDF27", // Unicode for 🌧
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 5.dp)
                            )

                        }
                    }
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.1f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))
                    Row(//5
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFB2EBF2), Color(0xFF80DEEA))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Weather Icon",
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Day 5",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                            )
                            Text(
                                text = "Friday",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = Color(0xFF616161)
                                )
                            )
                            // "Rain expected" box
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Chances of showers",
                                    fontSize = 8.sp,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = "\uD83C\uDF27", // Unicode for 🌧
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 5.dp)
                            )

                        }
                    }
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.1f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))
                    Row(//6
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFB2EBF2), Color(0xFF80DEEA))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Weather Icon",
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Day 6",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                            )
                            Text(
                                text = "Saturday",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = Color(0xFF616161)
                                )
                            )
                            Text(
                                text = "\uD83C\uDF27", // Unicode for 🌧
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 5.dp)
                            )
                        }
                    }
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.1f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))
                    Row(//7
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFB2EBF2), Color(0xFF80DEEA))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Weather Icon",
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Day 7",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                            )
                            Text(
                                text = "Sunday",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = Color(0xFF616161)
                                )
                            )
                            // "Rain expected" box
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Cloudy",
                                    fontSize = 8.sp,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = "\u2601",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 5.dp)
                            )

                        }
                    }
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.1f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Altitude-based Tips",
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(5.dp))



                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    TouristGuideTheme {
        val fakeNavController = rememberNavController()
        WeatherScreen(navController = fakeNavController)
    }
}