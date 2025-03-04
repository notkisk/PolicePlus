package com.example.policeplus.views.components

import Car
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.policeplus.R
import com.example.policeplus.ui.theme.PolicePlusBlue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScanDetailsPopup(car: Car, isPopup: Boolean = true, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PolicePlusBlue,
        shape = RoundedCornerShape(21.dp),
        modifier = Modifier.width(500.dp),
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Close button
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isPopup) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Outlined.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Icon
                Icon(
                    painter = painterResource(R.drawable.id_card),
                    contentDescription = "ID Icon",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Owner Info
                Text(
                    text = car.owner ?: "Unknown Owner",
                    color = Color(0xFFE7E7E7),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(200.dp)
                )
                Text(text = car.licenseNumber, color = Color.White, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(12.dp))

                // Vehicle & Owner Details
                val details = listOf(
                    "Make & Model:" to (car.makeAndModel ?: "Unknown"),
                    "Color:" to (car.color ?: "Unknown"),
                    "Owner Address:" to (car.address ?: "Unknown"),
                    "Driver’s License:" to (car.driverLicense ?: "Unknown")
                )

                details.forEach { (label, value) ->
                    InfoRowPopup(label, value)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Registration Status Section with Badges
                val registrationDetails = listOf(
                    "Insurance Status:" to getValidityStatus(car.insuranceEnd),
                    "Inspection Status:" to getValidityStatus(car.inspectionEnd),
                    "Tax Status:" to (car.taxPaid ?: "Unknown"),
                    "Stolen Car:" to car.stolenCar
                )

                registrationDetails.forEach { (label, value) ->
                    InfoRowPopup(label, value, getStatusBadge(value))
                }
            }
        },
        confirmButton = {} // No extra button needed
    )
}

// Function to check if a date is valid or expired
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

// Reusable Row with optional status badge
@Composable
fun InfoRowPopup(label: String, value: String, badge: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                color = Color(0xFFE7E7E7),
                fontSize = 16.sp,
                textAlign = TextAlign.Start
            )
            badge?.invoke()
        }
    }
}

// Returns a composable function for the status badge
@Composable
fun getStatusBadge(value: String): @Composable (() -> Unit)? {
    val color = when (value) {
        "Expired","Not Paid" -> Color.Red
        "Valid", "Paid","No" -> Color.Green
        "Yes" -> Color.Red
        else -> Color.Gray
    }

    return {
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .background(color, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(value, color = Color.White, fontSize = 12.sp)
        }
    }
}
