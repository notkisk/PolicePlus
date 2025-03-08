package com.example.policeplus.views

import Car
import com.example.policeplus.CarViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.policeplus.toEntity
import com.example.policeplus.ui.theme.PolicePlusBlue
import com.example.policeplus.ui.theme.Titles
import com.example.policeplus.views.components.RecentScanCard



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(viewModel: CarViewModel) {
    val carHistory by viewModel.carHistory.collectAsState()

    // Search query state
    var searchQuery by remember { mutableStateOf("") }

    val filteredHistory by remember {
        derivedStateOf {
            carHistory.filter { car ->
                car.licenseNumber.contains(searchQuery, ignoreCase = true) ||
                        car.owner.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(
                text = "History",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Titles
            )
            Spacer(Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by Plate or Owner") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                singleLine = true,
                shape = RoundedCornerShape(12),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = PolicePlusBlue,
                    unfocusedIndicatorColor = Color(0xFFECECEC)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )

            Spacer(Modifier.height(20.dp))

            LazyColumn {
                items(filteredHistory) { carEntity ->
                    // Convert CarEntity to Car for UI display
                    RecentScanCard(carEntity)
                    Spacer(Modifier.height(27.dp))
                }
            }
        }
    }
}
