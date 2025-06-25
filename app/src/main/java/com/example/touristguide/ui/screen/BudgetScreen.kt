package com.example.touristguide.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(navController: NavController) {
    var days by remember { mutableStateOf("") }
    var people by remember { mutableStateOf("") }


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
        ) {
            Column (
                modifier = Modifier
                    .padding(5.dp)
            ){ Text(
                    text = "Trip Duration (Days)",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = days,
                    onValueChange = { input ->
                        days = input
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(10.dp),

                    placeholder = {
                        Text("Enter number of days")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Gray.copy(0.2f),
                        unfocusedContainerColor = Color.Gray.copy(0.2f),
                    )
                )
                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "No. of Travelers",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                OutlinedTextField(//2
                    value = days,
                    onValueChange = { input ->
                        days = input
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(10.dp),

                    placeholder = {
                        Text("Enter number of days")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Gray.copy(0.2f),
                        unfocusedContainerColor = Color.Gray.copy(0.2f),
                    )
                )
                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "Select Region",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(
                    modifier = Modifier.height(5.dp)
                )

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
                            text = "Pokhara"
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
                            text = "Kathmandu"
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
                            text = "Mustang"
                        )
                    }
                }

                Text(
                    text = "Travel Style",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Button(
                    onClick = {},
                    modifier = Modifier,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color.Black
                    )

                ) {
                    Text(
                        text = "Estimate Cost"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Estimated Food Cost",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "Rs. 10,500",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light
                    )
                )

                Spacer(modifier = Modifier.height(25.dp))


                Text(
                    text = "Estimated Stay Cost",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "Rs. 15,000",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light
                    )
                )

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = "Estimated Transport Cost",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "Rs. 33,000",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(100.dp)
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
                            text = "Offine Mode"
                        )
                    }

                    Button(
                        onClick = {},
                        modifier = Modifier,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Save Estimate"
                        )
                    }

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