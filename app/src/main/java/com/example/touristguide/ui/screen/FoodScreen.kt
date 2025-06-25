package com.example.touristguide.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Food & Restaurants",
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
        ) {
            Column (
                modifier = Modifier
                    .padding(2.dp)
            ){
                HorizontalDivider(
                    color = Color.Black.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween

                ){
                    Text(
                        text = "Resturant",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.White
                        )

                    ) {
                        Text(
                            text = "Veiw Details",
                            fontSize = 15.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            tint = Color.Black,
                            contentDescription = null,
                            modifier = Modifier
                                .height(25.dp)
                                .width(25.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) { //Left side
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.LightGray)
                        ) {
                            // Replace with Image(...) to load weather icon
                        }
                        Spacer(modifier = Modifier.width(2.dp))
                        Column {
                            Text(
                                text = "Annapura Thakali Ghar",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "thakali",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            )
                        }

                    }

                    //Right side
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Rs /" +
                                        "500",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "person",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 12.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorite",
                            tint = Color.Red
                        )
                    }
                }
                HorizontalDivider(
                    color = Color.Black.copy(alpha = 1.0f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) { //Left side
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.LightGray)
                        ) {
                            // Replace with Image(...) to load weather icon
                        }
                        Spacer(modifier = Modifier.width(2.dp))
                        Column {
                            Text(
                                text = "Newari Bhanchha Ghar",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Newari",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            )
                        }

                    }

                    //Right side
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Rs /" +
                                        "500",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "person",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 12.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorite",
                            tint = Color.Red
                        )
                    }
                }
                HorizontalDivider(
                    color = Color.Black.copy(alpha = 1.0f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) { //Left side
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.LightGray)
                        ) {
                            // Replace with Image(...) to load weather icon
                        }
                        Spacer(modifier = Modifier.width(2.dp))
                        Column {
                            Text(
                                text = "Indian Spice",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Indian",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            )
                        }

                    }

                    //Right side
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Rs /" +
                                        "500",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "person",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 12.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorite",
                            tint = Color.Red
                        )
                    }
                }
                HorizontalDivider(
                    color = Color.Black.copy(alpha = 1.0f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Filters",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.LightGray
                        )

                    ) {
                        Text(
                            text = "Thakali"
                        )
                    }

                    Button(
                        onClick = {},
                        modifier = Modifier,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.LightGray
                        )

                    ) {
                        Text(
                            text = "Newari"
                        )
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Indian"
                        )
                    }
                }
                Row {
                    Button(
                        onClick = {},
                        modifier = Modifier,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = "Non-veg ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = "Veg",
                            fontSize = 20.sp,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Row {
                    Text(
                        text = "Budget Range",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold

                    )

                }

                LazyRow {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .height(70.dp)
                                .width(150.dp)
                                .background(color = Color.DarkGray)
                                .clip(RoundedCornerShape(25.dp))

                        ){
                            Text(
                                text = "Low Budget",
                                fontSize = 15.sp,
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }



                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .height(70.dp)
                                .width(150.dp)
                                .background(color = Color.DarkGray)
                        ){
                            Text(
                                text = "Medium Budget",
                                fontSize = 15.sp,
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }


                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .height(70.dp)
                                .width(150.dp)
                                .background(color = Color.DarkGray)

                        ){
                            Text(
                                text = "High Budget",
                                fontSize = 15.sp,
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

            }
        }

    }

    }


@Preview(showBackground = true)
@Composable
fun FoodScreenPreview() {
    TouristGuideTheme {
        val fakeNavController = rememberNavController()
        FoodScreen(navController = fakeNavController)
    }
}