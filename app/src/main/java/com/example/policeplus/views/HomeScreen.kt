package com.example.policeplus.views

import TicketDraftViewModel
import com.example.policeplus.CarViewModel
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.policeplus.R
import com.example.policeplus.UserViewModel
import com.example.policeplus.ui.theme.InterFont
import com.example.policeplus.ui.theme.PolicePlusBlue
import com.example.policeplus.ui.theme.Titles
import com.example.policeplus.views.components.RecentScanCard

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(viewModel: CarViewModel, onSearch: () -> Unit, navController:NavController, userViewModel: UserViewModel, draftViewModel:TicketDraftViewModel) {


    val userType = userViewModel.localUser.observeAsState().value?.userType

    if(userType == "police"){
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

            val officerName = userViewModel.localUser.value?.name?.split(" ")?.first()
            // Welcome Message
            Text(
                text = "Welcome, Officer ${officerName}! 👋",
                fontFamily = InterFont,
                fontSize = 20.sp, color = Titles, fontWeight = FontWeight.Medium, maxLines = 1
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


        SpeedDialFab(navController,draftViewModel,viewModel,userViewModel)
    }else{
        CarDataScreen(
            viewModel
        )
        SpeedDialFab(navController,draftViewModel,viewModel,userViewModel)

    }


}




@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedDialFab(navController: NavController, draftViewModel: TicketDraftViewModel = viewModel(), carViewModel: CarViewModel, userViewModel: UserViewModel) {

    var isExpanded by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = isExpanded, label = "FAB Transition")

    val fabShape by transition.animateDp(label = "FAB Shape") { expanded ->
        if (expanded) 28.dp else 12.dp
    }

    val iconRotation by transition.animateFloat(label = "FAB Icon Rotation") { expanded ->
        if (expanded) 45f else 0f
    }

    var showTicketDrawer by remember { mutableStateOf(false) }
    var showReportDrawer by remember { mutableStateOf(false) }
    var resumeDraft by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(300)) + slideInVertically(tween(300), initialOffsetY = { it }),
                exit = fadeOut(tween(200)) + slideOutVertically(tween(200), targetOffsetY = { it })
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    if (draftViewModel.hasDraft) {
                        MiniFab(icon = Icons.Default.Refresh, label = "Resume Draft") {
                            isExpanded = false
                            resumeDraft = true
                            showTicketDrawer = true
                        }
                    }
                    if(userViewModel.localUser.value?.userType == "police"){
                        MiniFab(icon = Icons.Default.Edit, label = "Create A Ticket") {
                            draftViewModel.clearDraft()
                            isExpanded = false
                            resumeDraft = false
                            showTicketDrawer = true
                        }
                    }

                    MiniFab(icon = Icons.Default.Warning, label = "Report A Car") {
                        isExpanded = false
                        showReportDrawer = true
                    }
                }
            }

            FloatingActionButton(
                onClick = { isExpanded = !isExpanded },
                containerColor = PolicePlusBlue,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .size(56.dp),
                shape = RoundedCornerShape(fabShape),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (isExpanded) "Close" else "Open Menu",
                    modifier = Modifier.rotate(iconRotation)
                )
            }
        }
    }

    if (showTicketDrawer) {
        TicketFormDrawer(
            onClose = { showTicketDrawer = false },
            resumeDraft = resumeDraft,
            draftViewModel = draftViewModel,carViewModel = carViewModel,userViewModel
        )
    }
    if (showReportDrawer) {
        ReportStolenCarDrawer(
            onClose = { showReportDrawer = false },
           carViewModel = carViewModel,userViewModel
        )
    }

    if (draftViewModel.hasDraft && !showTicketDrawer) {
        Snackbar(
            dismissAction = {
                Text(
                    text = "Dismiss",
                    color = Color(0xFFE74F4F),
                    modifier = Modifier.clickable {
                        resumeDraft = false
                        draftViewModel.clearDraft()
                    }.padding(horizontal = 15.dp)
                )
            },
            action = {
                Text(
                    text = "Resume",
                    color = PolicePlusBlue,
                    modifier = Modifier.clickable {
                        showTicketDrawer = true
                        resumeDraft = true
                    }
                )
            },
            modifier = Modifier.padding(bottom = 80.dp)
        ) { Text("Draft saved") }
    }
}

@Composable
fun MiniFab(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.98f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 8.dp)
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PolicePlusBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

