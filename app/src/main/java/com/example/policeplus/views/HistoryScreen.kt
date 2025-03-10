package com.example.policeplus.views

import com.example.policeplus.CarViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.policeplus.R
import com.example.policeplus.toEntity
import com.example.policeplus.ui.theme.PolicePlusBlue
import com.example.policeplus.ui.theme.Titles
import com.example.policeplus.views.components.RecentScanCard



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(viewModel: CarViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadUserAndHistory()
    }

    val carHistory by viewModel.carHistory.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredHistory by remember {
        derivedStateOf {
            carHistory.filter {
                it.licenseNumber.contains(searchQuery, ignoreCase = true) ||
                        it.owner.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(16.dp)
    ) {
        Text(
            text = "Scan History",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Titles,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by Plate or Owner") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = PolicePlusBlue
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                ,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = PolicePlusBlue,
                unfocusedIndicatorColor = Color(0xFFE0E0E0)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (searchQuery.isNotEmpty()) {
            Text(
                text = "Found ${filteredHistory.size} result(s)",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
        }

        if (filteredHistory.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_history_24),
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = if (searchQuery.isEmpty()) "No scan history available." else "No results found.",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredHistory) { carEntity ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(),
                        exit = fadeOut()
                    ) {
                        RecentScanCard(carEntity)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
