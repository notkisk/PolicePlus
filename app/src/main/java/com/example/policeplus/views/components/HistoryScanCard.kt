package com.example.policeplus.views.components

import UserPreferences
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.policeplus.R
import com.example.policeplus.models.Car
import com.example.policeplus.models.Ticket
import com.example.policeplus.models.User
import com.example.policeplus.views.InfoRow
import com.example.policeplus.views.SectionHeader
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale



fun formatTimestamp2(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScanCard(car: Car, navController: NavController, onDelete:()->Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val userPreferences = remember { UserPreferences(context) }
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        user = userPreferences.getUser()
    }

    val officerName = user?.name ?: "Unknown"

    fun parseDateString(dateString: String?): Long? {
        return try {
            val instant = Instant.parse(dateString)
            instant.toEpochMilli()
        } catch (e: Exception) {
            null
        }
    }

    val insuranceExpired = parseDateString(car.insuranceEnd)?.let {
        LocalDate.now().isAfter(
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        )
    } ?: false

    val inspectionExpired = parseDateString(car.inspectionEnd)?.let {
        LocalDate.now().isAfter(
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        )
    } ?: false

    val taxUnpaid = car.taxPaid.lowercase() != "paid"
    val isStolen = car.stolenCar.lowercase() == "yes"

    val hasIssue = insuranceExpired || inspectionExpired || taxUnpaid || isStolen

    val statusText = when {
        isStolen -> "STOLEN"
        hasIssue -> "ISSUE"
        else -> "CLEAR"
    }

    val statusColor = when (statusText) {
        "STOLEN" -> Color(0xFFFFCDD2)
        "ISSUE" -> Color(0xFFFFF9C4)
        else -> Color(0xFFC8E6C9)
    }
    val statusTextColor = when (statusText) {
        "STOLEN" -> Color(0xFFD32F2F)
        "ISSUE" -> Color(0xFFFBC02D)
        else -> Color(0xFF10D97F)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { showDialog = true },
                onLongClick = { showMenu = true }
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFBBDEFB), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = car.licenseNumber,
                                color = Color(0xFF0D47A1),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(statusColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = statusTextColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.outline_directions_car_24), contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = car.makeAndModel ?: "Unknown Model",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.outline_calendar_today_24), contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    val insuranceText = parseDateString(car.insuranceEnd)?.let { formatTimestamp2(it) } ?: "N/A"
                    Row{
                        Text(
                            text = "Insurance: ",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = insuranceText,
                            fontSize = 14.sp,
                            color = Color.Black
                        )

                        Text(
                            text = if (insuranceExpired) " (Expired)" else " (Valid)",
                            fontSize = 14.sp,
                            color = if (insuranceExpired) Color.Red else Color(0xFF10D97F),fontWeight = FontWeight.SemiBold
                        )
                    }

                }
                Spacer(modifier = Modifier.height(8.dp))


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    val inspectionText = parseDateString(car.inspectionEnd)?.let { formatTimestamp2(it) } ?: "N/A"

                    Row{
                        Text(
                            text = "Inspection: ",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = inspectionText,
                            fontSize = 14.sp,
                            color = Color.Black
                        )

                        Text(
                            text = if (inspectionExpired) " (Expired)" else " (Valid)",
                            fontSize = 14.sp,
                            color = if (inspectionExpired) Color.Red else Color(0xFF10D97F), fontWeight = FontWeight.SemiBold
                        )
                    }
                }

             /*   Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.outline_attach_money_24), contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tax Paid: ${car.taxPaid ?: "N/A"}" + if (taxUnpaid) " (Unpaid)" else "",
                        fontSize = 14.sp,
                        color = if (taxUnpaid) Color.Red else Color.Black
                    )
                }*/

                Spacer(modifier = Modifier.height(4.dp))
            Row{
                Text(
                    text = "Owner: ",
                    fontSize = 14.sp,
                    color = Color.Gray, fontWeight = FontWeight.Medium
                )
                Text(
                    text = car.owner ?: "Unknown",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }


                if (hasIssue) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFAE3E5), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = listOfNotNull(
                                if (isStolen) "STOLEN" else null,
                                if (insuranceExpired) "INSURANCE EXPIRED" else null,
                                if (inspectionExpired) "INSPECTION EXPIRED" else null,
                                if (taxUnpaid) "TAX UNPAID" else null
                            ).joinToString(", "),
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold, fontSize = 12.sp
                        )
                    }
                }
            }
        }
        //TicketsSection(car, ::formatDateDisplay)
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("View Details") },
                onClick = {
                    showMenu = false
                    showDialog = true
                }
            )
            DropdownMenuItem(
                text = { Text("Copy License Number") },
                onClick = {
                    clipboardManager.setText(AnnotatedString(car.licenseNumber))
                    showMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Delete from History") },
                onClick = {
                    showMenu = false
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    onDelete()
                }
            )
        }
    }

    AnimatedVisibility(
        visible = showDialog,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        ScanDetailsPopup(
            car = car,
            officerName = officerName,
            scanDate = formatTimestamp2(System.currentTimeMillis()),
            onDismiss = { showDialog = false },
            navController = navController
        )
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
