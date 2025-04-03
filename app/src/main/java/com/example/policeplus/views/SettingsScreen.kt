package com.example.policeplus.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.policeplus.R
import com.example.policeplus.UserViewModel
import com.example.policeplus.settings.SettingsDataStore
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val scope = rememberCoroutineScope()
    
    var showTerms by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }
    
    // Start with null and update when collected
    var notificationsEnabled by remember { mutableStateOf<Boolean?>(null) }
    
    // Collect notification preference
    LaunchedEffect(Unit) {
        settingsDataStore.notificationsEnabled.collect { enabled ->
            notificationsEnabled = enabled
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0077B6)
            )
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    SettingsSection(title = "Preferences") {
                        // Only show the switch if we have loaded the preference
                        notificationsEnabled?.let { enabled ->
                            SettingsSwitch(
                                title = "Push Notifications",
                                description = "Get notified about car status updates",
                                icon = R.drawable.ic_notification,
                                checked = enabled,
                                onCheckedChange = { newValue ->
                                    scope.launch {
                                        settingsDataStore.setNotificationsEnabled(newValue)
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    SettingsSection(title = "Legal") {
                        SettingsButton(
                            title = "Terms of Service",
                            description = "Read our terms of service",
                            icon = R.drawable.outline_description_24,
                            onClick = { showTerms = true }
                        )
                        SettingsButton(
                            title = "Privacy Policy",
                            description = "View our privacy policy",
                            icon = R.drawable.outline_shield_24,
                            onClick = { showPrivacy = true }
                        )
                    }
                }

                item {
                    SettingsSection(title = "About") {
                        SettingsButton(
                            title = "App Version",
                            description = "1.0.0",
                            icon = R.drawable.outline_info_24,
                            onClick = {}
                        )
                    }
                }

                // Add bottom padding
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    if (showTerms) {
        LegalDialog(
            title = "Terms of Service",
            content = """
                Terms of Service for PolicePlus App

                1. Acceptance of Terms
                By accessing and using the PolicePlus app, you agree to these terms of service.

                2. Description of Service
                PolicePlus provides a platform for managing and monitoring vehicle information, including:
                - Vehicle registration details
                - Insurance status
                - Tax payment status
                - Vehicle inspection records

                3. User Responsibilities
                - Provide accurate information
                - Maintain account security
                - Comply with local laws and regulations
                - Use the app responsibly

                4. Data Usage
                We collect and process data as outlined in our Privacy Policy.

                5. Service Modifications
                We reserve the right to modify or discontinue services with reasonable notice.

                6. Limitation of Liability
                The app is provided "as is" without warranties of any kind.

                7. Termination
                We may terminate service for violations of these terms.

                8. Governing Law
                These terms are governed by applicable local laws.

                Last updated: April 2025
            """.trimIndent(),
            onDismiss = { showTerms = false }
        )
    }

    if (showPrivacy) {
        LegalDialog(
            title = "Privacy Policy",
            content = """
                Privacy Policy for PolicePlus App

                1. Information We Collect
                - Personal information (name, email)
                - Vehicle information
                - Usage data
                - Location data (when required)

                2. How We Use Your Information
                - Provide and maintain services
                - Send notifications about vehicle status
                - Improve our services
                - Comply with legal requirements

                3. Data Security
                We implement security measures to protect your information.

                4. Data Sharing
                We do not share personal information with third parties except:
                - With your consent
                - For legal requirements
                - With service providers

                5. Your Rights
                You have the right to:
                - Access your data
                - Correct inaccurate data
                - Request data deletion
                - Opt-out of communications

                6. Notifications
                We send notifications about:
                - Insurance expiration
                - Tax status
                - Vehicle inspection
                - Important updates

                7. Contact Us
                For privacy concerns, contact: privacy@policeplus.com

                Last updated: April 2025
            """.trimIndent(),
            onDismiss = { showPrivacy = false }
        )
    }
}

@Composable
fun LegalDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0077B6)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = content,
                    fontSize = 14.sp,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = Color(0xFF0077B6))
                }
            }
        }
    }
}

@Composable
fun SettingsButton(
    title: String,
    description: String? = null,
    icon: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color(0xFF0077B6),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            if (description != null) {
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0077B6),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsSwitch(
    title: String,
    description: String? = null,
    icon: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color(0xFF0077B6),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            if (description != null) {
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF0077B6),
                checkedTrackColor = Color(0xFF0077B6).copy(alpha = 0.5f)
            )
        )
    }
}
