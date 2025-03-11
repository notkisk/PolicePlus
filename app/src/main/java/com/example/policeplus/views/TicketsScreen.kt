
package com.example.policeplus.views

import TicketDraftViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.policeplus.ui.theme.PolicePlusBlue
import com.example.policeplus.ui.theme.Titles
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketFormDrawer(
    onClose: () -> Unit,
    resumeDraft: Boolean = false,
    draftViewModel: TicketDraftViewModel = viewModel()
) {
    var licenseNumber by remember { mutableStateOf(draftViewModel.licenseNumber) }
    var selectedFamily by remember { mutableStateOf(draftViewModel.selectedFamily) }
    var ticketDetails by remember { mutableStateOf(draftViewModel.ticketDetails) }

    val ticketFamilies = listOf(
        "Speeding", "Parking Violation", "Reckless Driving", "Expired License",
        "Expired Insurance/Inspection", "Unpaid Tax", "No Seatbelt", "Other"
    )

    var dropdownExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Sync to ViewModel
    LaunchedEffect(licenseNumber, selectedFamily, ticketDetails) {
        draftViewModel.licenseNumber = licenseNumber
        draftViewModel.selectedFamily = selectedFamily
        draftViewModel.ticketDetails = ticketDetails
    }

    ModalBottomSheet(
        onDismissRequest = { onClose() },
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Issue a Ticket", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Titles)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = licenseNumber,
                onValueChange = { licenseNumber = it },
                label = { Text("License Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedFamily,
                    onValueChange = {},
                    label = { Text("Ticket Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    ticketFamilies.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selectedFamily = item
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ticketDetails,
                onValueChange = { ticketDetails = it },
                label = { Text("Details (Optional)") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (licenseNumber.isNotBlank() && selectedFamily.isNotBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("✅ Ticket submitted successfully")
                            draftViewModel.clearDraft()
                            onClose()
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("⚠️ Please fill all required fields")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PolicePlusBlue)
            ) {
                Text("Submit Ticket", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}
