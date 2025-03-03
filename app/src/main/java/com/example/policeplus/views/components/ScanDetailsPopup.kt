package com.example.policeplus.views.components

import Car
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

                // Car details
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
                // Car data list
                val details = listOf(
                    "Insurance Start Date:" to formatDate(car.insuranceStart ?: "Unknown"),
                    "Insurance End Date:" to formatDate(car.insuranceEnd ?: "Unknown"),
                    "Inspection Start Date:" to formatDate(car.inspectionStart ?: "Unknown"),
                    "Inspection End Date:" to formatDate(car.inspectionEnd ?: "Unknown"),
                    "Tax Status:" to (car.taxPaid ?: "Unknown"),
                    "Stolen Car:" to (car.stolen_car)
                )

                details.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text(
                            text = value,
                            color = Color(0xFFE7E7E7),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {} // No extra button needed
    )
}

