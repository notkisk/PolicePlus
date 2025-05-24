package com.example.policeplus.views

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.policeplus.CarViewModel
import com.example.policeplus.R
import com.example.policeplus.UserViewModel
import com.example.policeplus.models.Car
import com.example.policeplus.models.Ticket
import com.example.policeplus.models.CarEntity
import com.example.policeplus.toCar
import com.example.policeplus.views.components.ShimmerLoadingCard
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CarDataScreen(viewModel: CarViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var expandedCardId by remember { mutableStateOf<String?>(null) }

    val allCars by viewModel.allCars.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val error by viewModel.error.observeAsState(initial = "")
    val tickets by viewModel.tickets.collectAsState()
    val userViewModel: UserViewModel = hiltViewModel()
    val user by userViewModel.localUser.observeAsState()
    var showError by remember { mutableStateOf(true) }
    
    // Store the current car to show tickets for
    var currentCar by remember { mutableStateOf<Car?>(null) }
    
    // Tickets are now handled directly in the fetchCar function
    if (showDialog) {
        AddCarDialog(
            onDismiss = { showDialog = false },
            onAdd = { license ->
                viewModel.fetchCar(license)
                showDialog = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with gradient background
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF0077B6),
                                    Color(0xFF0096C7)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = if (user?.userType == "police") "Vehicle Details" else "My Cars",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (user?.userType == "police") 
                                "View and manage vehicle information" 
                            else 
                                "Manage your registered vehicles",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Add new car button for normal users
            if (user?.userType != "police") {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0077B6)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add New Car", color = Color.White)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF0077B6)
                    )
                }
            }  else if (allCars.isEmpty()) {
                EmptyState(user?.userType == "police")
            } else {
                if (error.isNotEmpty() && showError) {
                    ErrorMessage(error, onDismiss = { showError = false })
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    if(user?.userType != "police") {
                        items(allCars) { carEntity ->
                            val car = carEntity.toCar()
                            val carWithTickets = if (car.licenseNumber == viewModel.car.value?.licenseNumber) {
                                // Use the car data from the ViewModel which contains the tickets
                                viewModel.car.value ?: car
                            } else {
                                car
                            }
                            
                            if (allCars.size == 1) {
                                // Single car view for normal users - show details directly
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        currentCar = carWithTickets
                                        Log.d("CarDataScreen", "Displaying car with ${carWithTickets.tickets.size} tickets")
                                        Log.d("CarDataScreen", "Tickets: ${carWithTickets.tickets}")
                                        ScanDetails(
                                            car = carWithTickets,
                                            viewModel = viewModel
                                        )
                                    }
                                }
                            } else {
                                val carWithTickets = if (car.licenseNumber == viewModel.car.value?.licenseNumber) {
                                    // Use the car data from the ViewModel which contains the tickets
                                    viewModel.car.value ?: car
                                } else {
                                    car
                                }
                                currentCar = carWithTickets
                                Log.d("CarDataScreen", "Displaying car card with ${carWithTickets.tickets.size} tickets")
                                CarCard(
                                    car = carWithTickets,
                                    expanded = expandedCardId == car.licenseNumber,
                                    onClick = {
                                        expandedCardId = if (expandedCardId == car.licenseNumber) null else car.licenseNumber
                                    },
                                    viewModel = viewModel
                                )
                            }
                        }
                    } else {
                        item {
                            if (allCars.isNotEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        val lastCar = allCars.last().toCar()
                                        val carWithTickets = if (viewModel.car.value != null) {
                                            // Use the car from ViewModel which has tickets
                                            viewModel.car.value!!
                                        } else {
                                            lastCar
                                        }
                                        currentCar = carWithTickets
                                        Log.d("PoliceView", "Displaying car with ${carWithTickets.tickets.size} tickets")
                                        Log.d("PoliceView", "Tickets: ${carWithTickets.tickets}")
                                        ScanDetails(
                                            car = carWithTickets,
                                            viewModel = viewModel
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun AddCarDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var newLicense by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Car") },
        text = {
            TextField(
                value = newLicense,
                onValueChange = { newLicense = it },
                label = { Text("License Plate") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newLicense.isNotBlank()) {
                        onAdd(newLicense)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CarCard(
    car: Car,
    expanded: Boolean,
    onClick: () -> Unit,
    viewModel: CarViewModel
) {
    val carToDelete by viewModel.carToDelete.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (car.stolenCar == "Yes") Color(0xFFFFF3E0) else Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = car.licenseNumber,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (car.stolenCar == "Yes") Color(0xFFF57C00) else Color(0xFF0077B6)
                    )
                    Text(
                        text = car.makeAndModel,
                        fontSize = 16.sp,
                        color = if (car.stolenCar == "Yes") Color(0xFFF57C00) else Color(0xFF757575)
                    )
                    if (car.stolenCar == "Yes") {
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = "Stolen car warning",
                                tint = Color(0xFFF57C00),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Reported Stolen",
                                fontSize = 14.sp,
                                color = Color(0xFFF57C00),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    IconButton(
                        onClick = {
                            viewModel.showDeleteDialogForCar(car)
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = Color(0xFFFFEBEE),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete car",
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) "Show less" else "Show more",
                        tint = if (car.stolenCar == "Yes") Color(0xFFF57C00) else Color(0xFF0077B6)
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = if (car.stolenCar == "Yes") Color(0xFFFFE0B2) else Color(0xFFE0E0E0))
                Spacer(modifier = Modifier.height(16.dp))
                ScanDetails(car, viewModel)
            }
        }
    }

    // Only show dialog for the selected car
    if (carToDelete?.id == car.id) {
        Log.d("car data", "car license number: ${car.licenseNumber}, car id: ${car.id}")
        AlertDialog(
            onDismissRequest = { viewModel.cancelDelete() },
            title = { Text("Remove Car") },
            text = {
                Text("Are you sure you want to remove ${car.makeAndModel} (${car.licenseNumber})?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.confirmDeleteCar()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE53935))
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.cancelDelete() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF0077B6))
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScanDetails(car: Car, viewModel: CarViewModel) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    LaunchedEffect(Unit) {
        Log.d("TICKET_DEBUG", "Car tickets: ${car.tickets}")
        Log.d("TICKET_DEBUG", "Car license: ${car.licenseNumber}")
    }
    
    fun formatDate(dateString: String?): String {
        return dateString?.let {
            try {
                Instant.parse(it).atZone(ZoneId.of("UTC")).toLocalDate().format(formatter)
            } catch (e: Exception) {
                "Invalid Date"
            }
        } ?: "Unknown"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        VehicleInfoSection(car)
        OwnerInfoSection(car)
        RegistrationStatusSection(car, ::formatDate)
        
        // Always show tickets section with the car's tickets
        TicketsSection(tickets = car.tickets, formatDate = ::formatDate)

        if (car.stolenCar == "Yes") {
            StolenCarAlert()
        }
    }
}

@Composable
fun VehicleInfoSection(car: Car) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader("🧾 Vehicle Info")
            InfoRow("Make & Model:", car.makeAndModel ?: "Unknown")
            InfoRow("Color:", car.color ?: "Unknown")
        }
    }
}

@Composable
fun OwnerInfoSection(car: Car) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader("👤 Owner Info")
            InfoRow("Name:", car.owner ?: "Unknown")
            InfoRow("Address:", car.address ?: "Unknown")
            InfoRow("Driver's License:", car.driverLicense ?: "Unknown")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistrationStatusSection(car: Car, formatDate: (String?) -> String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader("🛡️ Registration Info")
            InfoRow("Insurance:", formatDate(car.insuranceEnd), getInsuranceStatus(car.insuranceEnd))
            InfoRow("Inspection:", formatDate(car.inspectionEnd), getInspectionStatus(car.inspectionEnd))
            InfoRow("Tax Status:", car.taxPaid ?: "Unknown", if (car.taxPaid == "Paid") "Paid" else "Unpaid")
            InfoRow("Stolen Car:", car.stolenCar, if (car.stolenCar == "Yes") "Stolen" else "Safe")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TicketsSection(tickets: List<Ticket>, formatDate: (String?) -> String) {
    Log.d("TICKET_DEBUG", "Rendering TicketsSection with ${tickets.size} tickets")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🎫 Tickets (${tickets.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (tickets.isEmpty()) {
                Text(
                    text = "No tickets found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    tickets.forEach { ticket ->
                        Log.d("TICKET_DEBUG", "Rendering ticket: ${ticket.ticketType}")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Type: ${ticket.ticketType}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (ticket.details.isNotBlank()) {
                                    Text(
                                        text = "Details: ${ticket.details}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Date: ${formatDate(ticket.ticketDate)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TicketItem(ticket: Ticket, formatDate: (String?) -> String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        InfoRow("Ticket Type", ticket.ticketType)
        InfoRow("Details", ticket.details)
        InfoRow("Issue Date", formatDate(ticket.ticketDate))
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = Color(0xFFE0E0E0))
}

@Composable
fun InfoRow(label: String, value: String, status: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = Color.Gray, fontSize = 13.sp)
            Text(value, fontSize = 14.sp, color = Color.Black)
        }
        status?.let {
            BadgeStatus(it)
        }
    }
}

@Composable
fun BadgeStatus(status: String) {
    val bgColor = when (status) {
        "Expired", "Unpaid", "Stolen" -> Color(0xFFfae3e5)
        "Safe", "Valid", "Paid" -> Color(0xFFdcf2ed)
        else -> Color.LightGray
    }
    val textColor = when (status) {
        "Expired", "Unpaid", "Stolen" -> Color(0xFFef4444)
        "Safe", "Valid", "Paid" -> Color(0xFF10d981)
        else -> Color.DarkGray
    }
    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(status, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getInsuranceStatus(insuranceEnd: String?): String {
    return insuranceEnd?.let {
        val expiry = Instant.parse(it).atZone(ZoneId.of("UTC")).toLocalDate()
        if (expiry.isBefore(LocalDate.now())) "Expired" else "Valid"
    } ?: "Unknown"
}

@RequiresApi(Build.VERSION_CODES.O)
fun getInspectionStatus(inspectionEnd: String?): String {
    return inspectionEnd?.let {
        val expiry = Instant.parse(it).atZone(ZoneId.of("UTC")).toLocalDate()
        if (expiry.isBefore(LocalDate.now())) "Expired" else "Valid"
    } ?: "Unknown"
}

@Composable
fun StolenCarAlert() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF3B3B))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Outlined.Warning, contentDescription = null, tint = Color.White)
            Text("Stolen Car Alert", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "This car is reported as stolen. Follow department protocols immediately.",
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyState(isPolice: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_directions_car_24),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF9E9E9E)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isPolice) "No vehicles scanned yet" else "No cars added yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF424242)
        )
        Text(
            text = if (isPolice) 
                "Scan a license plate to view vehicle details" 
            else 
                "Add your car using the button above",
            fontSize = 14.sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ErrorMessage(message: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = message,
                    color = Color(0xFFB71C1C),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Dismiss",
                    tint = Color(0xFFB71C1C)
                )
            }
        }
    }
}