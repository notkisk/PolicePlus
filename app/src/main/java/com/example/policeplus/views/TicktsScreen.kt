
package com.example.policeplus.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TicketsScreen(navController: NavController) {
    var licenseNumber by remember { mutableStateOf("") }
    var selectedFamily by remember { mutableStateOf("") }
    var ticketDetails by remember { mutableStateOf("") }
    val ticketFamilies = listOf("Speeding", "Parking Violation", "Reckless Driving", "Expired License","Expired Insurance/Inspection","Unpaid Tax", "No Seatbelt", "Other")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Issue a Ticket", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            // License Number Input
            OutlinedTextField(
                value = licenseNumber,
                onValueChange = { licenseNumber = it },
                label = { Text("License Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ticket Family Dropdown
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedTextField(
                    value = selectedFamily,
                    onValueChange = {},
                    label = { Text("Ticket Family") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ticketFamilies.forEach { family ->
                        DropdownMenuItem(
                            text = { Text(family) },
                            onClick = {
                                selectedFamily = family
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ticket Details Input
            OutlinedTextField(
                value = ticketDetails,
                onValueChange = { ticketDetails = it },
                label = { Text("Ticket Details") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = { /* Handle Ticket Submission */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Ticket")
            }
        }
    }
}
