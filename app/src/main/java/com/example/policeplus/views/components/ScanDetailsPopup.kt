package com.example.policeplus.views.components

import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.policeplus.R
import com.example.policeplus.ui.theme.PolicePlusBlue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.example.policeplus.models.Car
import com.example.policeplus.models.CarEntity

import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
fun formatDateDisplay(dateString: String?): String {
    return try {
        val timestamp = Instant.parse(dateString).toEpochMilli()
        formatTimestamp2(timestamp)
    } catch (e: Exception) {
        "Unknown"
    }
}

// updated version
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScanDetailsPopup(car: Car, officerName: String, scanDate: String, onDismiss: () -> Unit, navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = car.owner ?: "Unknown Owner",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center, modifier = Modifier.clickable {
                    clipboardManager.setText(AnnotatedString(car.licenseNumber))
                }
            )
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .background(Color(0xFFFFCA0B), shape = RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = car.licenseNumber,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold, modifier = Modifier.clickable {
                        clipboardManager.setText(AnnotatedString(car.licenseNumber))
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val isCritical = getValidityStatus(car.insuranceEnd) == "Expired" || car.stolenCar == "Yes"
            if (isCritical) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF44336), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painterResource(R.drawable.outline_report_problem_24),
                            contentDescription = "alert",
                            tint = Color.White,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("This vehicle has critical issues", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🧾", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Vehicle Info", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    val details = listOf(
                        "Make & Model:" to (car.makeAndModel ?: "Unknown"),
                        "Color:" to (car.color ?: "Unknown"),
                        "📍 Owner Address:" to (car.address ?: "Unknown"),
                        "Driver’s License:" to (car.driverLicense ?: "Unknown")
                    )
                    details.forEach { (label, value) ->
                        InfoRowPopup(label, value)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛡️", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Registration Info", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    val registrationDetails = listOf(
                        "Insurance Status:" to (formatDateDisplay(car.insuranceEnd) ?: "Unknown"),
                        "Inspection Status:" to (formatDateDisplay(car.inspectionEnd) ?: "Unknown"),
                        "Tax Status:" to (car.taxPaid ?: "Unknown"),
                        "Stolen Car:" to car.stolenCar
                    )
                    registrationDetails.forEach { (label, rawValue) ->
                        val status = when (label) {
                            "Insurance Status:" -> getValidityStatus(car.insuranceEnd)
                            "Inspection Status:" -> getValidityStatus(car.inspectionEnd)
                            "Tax Status:" -> if (car.taxPaid == "Paid") "Valid" else "Expired"
                            "Stolen Car:" -> if (car.stolenCar == "Yes") "Stolen" else "Valid"
                            else -> ""
                        }
                        InfoRowPopup(label, rawValue) {
                            if (status.isNotEmpty()) {
                                getStatusBadge(status) {}
                            }
                        }
                    }
                }
            }

            if (!car.tickets.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🚨", fontSize = 18.sp)
                            Spacer(Modifier.width(8.dp))
                            Text("Tickets (${car.tickets.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        car.tickets.forEachIndexed { index, ticket ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Ticket #${index + 1}: ${ticket.ticketType}",
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFFF6D00)
                                    )
                                    Text(
                                        text = formatDateDisplay(ticket.ticketDate) ?: "Unknown date",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ticket.details,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                                if (index < car.tickets.size - 1) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        thickness = 1.dp,
                                        color = Color.LightGray.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Scanned by Officer $officerName • $scanDate",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            val clipboardManager = LocalClipboardManager.current
            val context = LocalContext.current

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, buildCarInfoString(car))
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Vehicle Info"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Info")
                    }
                    Text("Share", fontSize = 12.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(buildCarInfoString(car)))
                        coroutineScope.launch { snackbarHostState.showSnackbar("Copied to clipboard") }
                    }) {
                        Icon(painterResource(R.drawable.outline_content_copy_24), contentDescription = "Copy Info")
                    }
                    Text("Copy", fontSize = 12.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = {
                        navController.navigate("scan")
                        Toast.makeText(context, "Capture Again clicked", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(painterResource(R.drawable.scan), contentDescription = "Capture Again", tint = Color.Unspecified)
                    }
                    Text("Rescan", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun getValidityStatus(dateString: String?): String {
    return dateString?.let {
        try {
            val expirationDate = Instant.parse(it).atZone(ZoneId.of("UTC")).toLocalDate()
            val currentDate = LocalDate.now()
            if (expirationDate.isAfter(currentDate)) "Valid" else "Expired"
        } catch (e: Exception) {
            "Unknown"
        }
    } ?: "Unknown"
}

@Composable
fun InfoRowPopup(label: String, value: String, badge: @Composable (() -> Unit)? = null) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(
                text = value,
                color = Color(0xFF2D2D2D),
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.clickable {
                    clipboardManager.setText(AnnotatedString(value))
                    Toast.makeText(context, "Copied: $value", Toast.LENGTH_SHORT).show()
                }
            )
        }
        if (badge != null) {
            Box(modifier = Modifier.padding(start = 8.dp)) {
                badge()
            }
        }
    }
}


fun getStatusBadge(value: String, onClick: () -> Unit): @Composable (() -> Unit)? {
    val bgColor = when (value) {
        "Expired", "Unpaid", "Stolen", "Not Paid", "Yes" -> Color(0xFFfae3e5)
        "Safe", "Valid", "Paid", "No" -> Color(0xFFdcf2ed)
        else -> Color.LightGray
    }
    val textColor = when (value) {
        "Expired", "Unpaid", "Stolen", "Not Paid", "Yes" -> Color(0xFFef4444)
        "Safe", "Valid", "Paid", "No" -> Color(0xFF10D97F)
        else -> Color.DarkGray
    }
    return {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .background(bgColor)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = value, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun buildCarInfoString(car: Car): String {
    return """
        Owner: ${car.owner ?: "Unknown"}
        License Number: ${car.licenseNumber}
        Make & Model: ${car.makeAndModel ?: "Unknown"}
        Color: ${car.color ?: "Unknown"}
        Address: ${car.address ?: "Unknown"}
        Driver’s License: ${car.driverLicense ?: "Unknown"}
        Insurance Status: ${getValidityStatus(car.insuranceEnd)}
        Inspection Status: ${getValidityStatus(car.inspectionEnd)}
        Tax Status: ${car.taxPaid ?: "Unknown"}
        Stolen Car: ${car.stolenCar}
    """.trimIndent()
}



