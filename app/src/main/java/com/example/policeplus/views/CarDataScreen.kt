package com.example.policeplus.views

import com.example.policeplus.CarViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.policeplus.models.Car
import com.example.policeplus.ui.theme.PolicePlusBlue
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CarDataScreen(viewModel: CarViewModel) {
    val car by viewModel.car.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState("")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(50.dp))
        Text(
            "Car Data",
            color = Color(0xFF5B5B5B),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(30.dp))

        if (isLoading) {
            CircularProgressIndicator(color = PolicePlusBlue)
        } else {
            car?.let {
                ScanDetails(car = it)
            } ?: Text( if (error.isNotBlank()) error else "No data available", color = Color.Gray, fontSize = 16.sp)
        }

    }
}

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

    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {
        LazyColumn {items(1){
            VehicleInfoSection(car)
            Spacer(modifier = Modifier.height(16.dp))
            OwnerInfoSection(car)
            Spacer(modifier = Modifier.height(16.dp))
            RegistrationStatusSection(car, ::formatDate)
            if (car.stolenCar == "Yes") {
                Spacer(modifier = Modifier.height(20.dp))
                StolenCarAlert()
            }
        }

        }

    }
}

@Composable
fun VehicleInfoSection(car: Car) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Vehicle Information", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)

            InfoRow("Make & Model:", car.makeAndModel?:"Unknown")
            InfoRow("Color:", car.color?:"Unknown")

        }
    }
}

@Composable
fun OwnerInfoSection(car: Car) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Owner", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)

            InfoRow("Name:", car.owner ?: "Unknown")
            InfoRow("Address:", car.address?: "Unknown")
            InfoRow("Driver's License:", car.driverLicense?: "Unknown")
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistrationStatusSection(car: Car, formatDate: (String?) -> String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Registration Status", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)

            InfoRow("Insurance:", formatDate(car.insuranceEnd), getInsuranceStatus(car.insuranceEnd))
            InfoRow("Inspection:", formatDate(car.inspectionEnd), getInspectionStatus(car.inspectionEnd))
            InfoRow("Tax Status:", "", if (car.taxPaid == "Paid") "Paid" else "Unpaid")
            InfoRow("Stolen Car:", "", if (car.stolenCar == "Yes") "Stolen" else "Safe")
        }
    }
}

@Composable
fun InfoRow( label: String, value: String, status: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(label, color = Color.Gray, fontWeight = FontWeight.Normal, fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = Color.Gray, fontSize = 16.sp, textAlign = TextAlign.Start)
            status?.let { BadgeStatus(it) }
        }
    }
}

@Composable
fun BadgeStatus(status: String) {
    val color = when (status) {
        "Expired","Unpaid","Stolen" -> Color(0xFFfae3e5)
        "Safe","Valid","Paid" -> Color(0xFFdcf2ed)
        else -> Color.Gray
    }
    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .background(color, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(status, color = if(status in listOf("Expired","Unpaid","Stolen"))  Color(0xFFef4444) else Color(0xFF10d981), fontSize = 12.sp)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getInsuranceStatus(insuranceEnd: String?): String {
    return insuranceEnd?.let {
        val expiryDate = Instant.parse(it).atZone(ZoneId.of("UTC")).toLocalDate()
        if (expiryDate.isBefore(LocalDate.now())) "Expired" else "Valid"
    } ?: "Unknown"
}

@RequiresApi(Build.VERSION_CODES.O)
fun getInspectionStatus(inspectionEnd: String?): String {
    return inspectionEnd?.let {
        val expiryDate = Instant.parse(it).atZone(ZoneId.of("UTC")).toLocalDate()
        if (expiryDate.isBefore(LocalDate.now())) "Expired" else "Valid"
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
            Icon(Icons.Outlined.Warning, contentDescription = "", tint = Color.White)
            Text(
                text = "Stolen Car Alert",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This car is reported as stolen! Please follow department protocols for handling stolen vehicles.",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}