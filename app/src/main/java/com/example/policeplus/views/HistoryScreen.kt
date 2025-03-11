package com.example.policeplus.views

import com.example.policeplus.CarViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.navigation.NavController
import com.example.policeplus.R
import com.example.policeplus.toEntity
import com.example.policeplus.ui.theme.InterFont
import com.example.policeplus.ui.theme.PolicePlusBlue
import com.example.policeplus.ui.theme.Titles
import com.example.policeplus.views.components.HistoryScanCard
import com.example.policeplus.views.components.RecentScanCard
import com.example.policeplus.views.components.getValidityStatus


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(viewModel: CarViewModel, navController: NavController) {
    LaunchedEffect(Unit) {
        viewModel.loadUserAndHistory()
    }

    val carHistory by viewModel.carHistory.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredHistory by remember {
        derivedStateOf {
            carHistory.filter {
                val matchesQuery = it.licenseNumber.contains(searchQuery, ignoreCase = true) || it.owner.contains(searchQuery, ignoreCase = true)
                val matchesFilter = when (selectedFilter) {
                    "Stolen" -> it.stolenCar.lowercase() == "yes"
                    "Issue" -> {
                        val insuranceExpired = getValidityStatus(it.insuranceEnd) == "Expired"
                        val inspectionExpired = getValidityStatus(it.inspectionEnd) == "Expired"
                        val taxUnpaid = it.taxPaid.lowercase() != "paid"
                        insuranceExpired || inspectionExpired || taxUnpaid || it.stolenCar.lowercase() == "yes"
                    }
                    else -> true
                }
                matchesQuery && matchesFilter
            }
        }
    }

    val stolenCount = carHistory.count { it.stolenCar.lowercase() == "yes" }
    val issueCount = carHistory.count {
        val insuranceExpired = getValidityStatus(it.insuranceEnd) == "Expired"
        val inspectionExpired = getValidityStatus(it.inspectionEnd) == "Expired"
        val taxUnpaid = it.taxPaid.lowercase() != "paid"
        insuranceExpired || inspectionExpired || taxUnpaid || it.stolenCar.lowercase() == "yes"
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

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by Plate or Owner", fontFamily = InterFont, color = Color(0xFFABABAB))  },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Search Icon", modifier = Modifier.size(20.dp), tint = Color(0xFF7C7C7C)
                )
            },
            modifier = Modifier.fillMaxWidth(),
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

        Spacer(modifier = Modifier.height(12.dp))

        Text("Filter:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            val filterOptions = listOf("All" to carHistory.size, "Stolen" to stolenCount, "Issue" to issueCount)
            filterOptions.forEachIndexed { index, (filter, count) ->
                val isSelected = selectedFilter == filter
                val bgColor = when (filter) {
                    "Stolen" -> if (isSelected) Color(0xFFFAE3E5) else Color.White
                    "Issue" -> if (isSelected) Color(0xFFFFF9C4) else Color.White
                    else -> if (isSelected) PolicePlusBlue else Color.White
                }
                val contentColor = when (filter) {
                    "Stolen" -> if (isSelected) Color(0xFFD32F2F) else Color(0xFFD32F2F)
                    "Issue" -> if (isSelected) Color(0xFFFBC02D) else Color(0xFFFBC02D)
                    else -> if (isSelected) Color.White else PolicePlusBlue
                }
                Button(
                    onClick = { selectedFilter = filter },
                    colors = ButtonDefaults.buttonColors(containerColor = bgColor, contentColor = contentColor),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text("$filter ($count)", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (searchQuery.isNotEmpty()) {
            Text(
                text = "Found ${filteredHistory.size} result(s)",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
        }

        if (filteredHistory.isEmpty()) {
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
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredHistory) { carEntity ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(),
                        exit = fadeOut()
                    ) {
                        HistoryScanCard(carEntity, navController) { viewModel.deleteACar(carEntity) }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}