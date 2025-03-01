package com.example.policeplus.views.components

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

@Composable
fun ScanDetailsPopup(name: String = "Haithem Bekkari",
                     carPlate:String = "5846712139",
                     insuranceStartDate:String="2024/10/08",
                     insuranceEndDate:String="2025/10/08",
                     inspectionDate:String="2024/10/08",
                     inspectionPeriod:String="2025/10/08",
                     taxStatus:String="Paid",
                     stolenCar:String="No"
                     , onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PolicePlusBlue,
        shape = RoundedCornerShape(21.dp), modifier = Modifier.width(500.dp),
        text = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 50.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = Color.White)
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.id_card),
                    contentDescription = "ID Icon",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(name, color = Color(0xFFE7E7E7), fontWeight = FontWeight.Medium, fontSize = 16.sp, textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(200.dp))
                Text(carPlate, color = Color.White, fontSize = 16.sp)
                Text("2024-02-24", color = Color(0xFFA7A7A7), fontSize = 11.sp)

                Spacer(modifier = Modifier.height(12.dp))

                val details = listOf(
                    "Insurance Start Date:" to insuranceStartDate,
                    "Insurance End Date:" to insuranceEndDate,
                    "Inspection Date:" to inspectionDate,
                    "Inspection Period:" to inspectionPeriod,
                    "Tax Status:" to taxStatus,
                    "Stolen Car:" to stolenCar
                )

                details.forEach { (label, value) ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(value, color = Color(0xFFE7E7E7), fontSize = 16.sp, textAlign = TextAlign.Start, modifier = Modifier.width(100.dp))
                    }
                }
            }
        },
        confirmButton = {} // No extra button needed
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewScanDetailsPopup() {
    MaterialTheme {
        ScanDetailsPopup(onDismiss = {}) // Dummy onDismiss function
    }
}
