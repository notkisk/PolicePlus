package com.example.policeplus.views

import com.example.policeplus.CarViewModel
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.policeplus.R
import com.example.policeplus.models.Car
import com.example.policeplus.toEntity
import com.example.policeplus.ui.theme.InterFont
import com.example.policeplus.ui.theme.PolicePlusBlue
import com.example.policeplus.ui.theme.Titles
import com.example.policeplus.views.components.RecentScanCard

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(viewModel: CarViewModel, onSearch: () -> Unit,navController:NavController) {


    LaunchedEffect(Unit) {
        viewModel.loadUserAndHistory()
    }
    var licensePlate by remember { mutableStateOf("") }
    val carData by viewModel.car.observeAsState() // Assuming it's StateFlow

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Logo
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "PolicePlus Logo",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Welcome Message
        Text(
            text = "Welcome, Officer Ali! 👋",
            fontFamily = InterFont,
            fontSize = 20.sp, color = Titles, fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = licensePlate,
            onValueChange = { licensePlate = it },
            placeholder = { Text("Enter car License Plate", fontFamily = InterFont, color = Color(0xFFABABAB)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Search Icon", modifier = Modifier.size(20.dp), tint = Color(0xFF7C7C7C)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = PolicePlusBlue,
                unfocusedIndicatorColor = Color(0xFFECECEC)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (licensePlate.isNotBlank()) {
                        viewModel.fetchCar(licensePlate)
                        onSearch()
                    }
                }
            )
        )

        Spacer(modifier = Modifier.height(24.dp))



        val recentScans by viewModel.latestScans.collectAsState()

// Show Recent Scans only if there are any
        if (recentScans.isNotEmpty()) {
            Text("Recent Scans:", fontSize = 22.sp, fontFamily = InterFont, fontWeight = FontWeight.Bold, color = Titles)
            Spacer(modifier = Modifier.height(27.dp))

            LazyColumn {
                items(recentScans) { car ->
                    RecentScanCard(car,navController,onDelete = { viewModel.deleteACar(car) })
                    Spacer(modifier = Modifier.height(27.dp))
                }
            }
        }else{

            Column {
                Text("Recent Scans:", fontSize = 22.sp, fontFamily = InterFont, fontWeight = FontWeight.Bold, color = Titles)
                Text("No Scanned Cars!", fontSize = 14.sp, fontFamily = InterFont, fontWeight = FontWeight.Light, color = Titles)
                Spacer(modifier = Modifier.height(27.dp))

            }
        }
        val carHistory by viewModel.carHistory.collectAsState() // Get history from ViewModel

        // Total Scanned Cars
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Total Scanned Cars:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Titles
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(PolicePlusBlue, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = carHistory.size.toString(), // Dynamically show scanned cars count
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }


        }
    }

Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd){

    SpeedDialFab()

}
}



@Composable
fun SpeedDialFab() {
    var isExpanded by remember{ mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()){
        Column(verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
        )

        {
            if(isExpanded){
                MiniFab(icon= Icons.Default.Edit, label = "Create A Ticket"){

                    isExpanded=false
                }
                MiniFab(icon= Icons.Default.Warning, label = "Report A Car"){
                    isExpanded=false
                }

            }
            FloatingActionButton(
                onClick = { isExpanded=!isExpanded },
                containerColor = PolicePlusBlue,
                contentColor = Color.White,
                modifier = Modifier.size(90.dp).padding(25.dp)
            ) {
                Text("+")
            }

        }

    }

}

@Composable
fun MiniFab(icon: ImageVector, label:String, onClick:()->Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End){
        Text(text=label,modifier=Modifier.padding(end=8.dp))
        FloatingActionButton(onClick = onClick, modifier = Modifier.size(40.dp), containerColor = MaterialTheme.colorScheme.secondary) {
            Icon(icon, contentDescription = label)
        }
    }
}


