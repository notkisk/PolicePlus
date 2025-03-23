package com.example.policeplus.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.policeplus.CarViewModel
import com.example.policeplus.UserViewModel
import com.example.policeplus.models.Car
import com.example.policeplus.models.Ticket
import com.example.policeplus.views.components.ShimmerLoadingCard
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CarDataScreen(viewModel: CarViewModel) {
    val car by viewModel.car.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val error by viewModel.error.observeAsState(initial = "")
    val userViewModel: UserViewModel = hiltViewModel()

    // Observe user and get license number (same as before)
    val user by userViewModel.localUser.observeAsState()
    val licenseNumber = remember(user) {
        if (user?.userType != "police") {
            user?.licenseNumber
        } else {
            null
        }
    }

    // Use a remember-ed flag to track if we've fetched
    var hasFetched by remember { mutableStateOf(false) }

    LaunchedEffect(licenseNumber) { // Key is licenseNumber (could also be Unit)
        if (licenseNumber != null && !hasFetched) {
            viewModel.fetchCar(licenseNumber)
            hasFetched = true // Set the flag to prevent future fetches
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            text = "Vehicle Details",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D2D),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        if (isLoading) {
            //CircularProgressIndicator(color = PolicePlusBlue)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ShimmerLoadingCard(120.dp, 2)
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ShimmerLoadingCard(180.dp, 3)
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ShimmerLoadingCard(200.dp, 4)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

        } else {
            if (car != null) {  // More concise null check
                ScanDetails(car!!) // Safe to use !! here because of the null check above
            } else {
                Text(
                    text = error.ifBlank { "No data available" }, // More idiomatic
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// ... rest of your composables ... (They are well-structured and don't need changes)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScanDetails(car: Car) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    fun formatDate(dateString: String?): String {
        return dateString?.let {
            try {
                Instant.parse(it).atZone(ZoneId.of("UTC")).toLocalDate().format(formatter)
            } catch (e: Exception) {
                "Invalid Date"
            }
        } ?: "Unknown"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { VehicleInfoSection(car) }
        item { OwnerInfoSection(car) }
        item { RegistrationStatusSection(car, ::formatDate) }
        item{TicketsSection(car, ::formatDate)}
        if (car.stolenCar == "Yes") item { StolenCarAlert() }
        item { Spacer(modifier = Modifier.height(16.dp)) }
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
fun TicketsSection(car: Car, formatDate: (String?) -> String) {
    if (car.tickets.isEmpty()) return // No tickets, skip section

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader("🎫 Tickets")

            car.tickets.forEachIndexed { index, ticket ->
                TicketItem(ticket, formatDate)

                // Divider between tickets
                if (index != car.tickets.lastIndex) {
                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TicketItem(ticket: Ticket, formatDate: (String?) -> String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
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