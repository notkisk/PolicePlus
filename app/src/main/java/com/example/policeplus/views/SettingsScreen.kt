package com.example.policeplus.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.policeplus.R
import com.example.policeplus.UserViewModel

@Composable
fun SettingsScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    var darkModeEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoScanEnabled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Appearance Section
                item {
                    SettingsSection(title = "Appearance") {
                        SettingsSwitch(
                            title = "Dark Mode",
                            description = "Enable dark mode for better visibility at night",
                            icon = R.drawable.outline_dark_mode_24,
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it }
                        )
                    }
                }

                // Notifications Section
                item {
                    SettingsSection(title = "Notifications") {
                        SettingsSwitch(
                            title = "Push Notifications",
                            description = "Get notified about important updates",
                            icon = R.drawable.outline_notifications_24,
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )
                    }
                }

                // Scanner Settings Section
                item {
                    SettingsSection(title = "Scanner") {
                        SettingsSwitch(
                            title = "Auto Scan",
                            description = "Automatically scan license plates when detected",
                            icon = R.drawable.outline_qr_code_scanner_24,
                            checked = autoScanEnabled,
                            onCheckedChange = { autoScanEnabled = it }
                        )
                    }
                }

                // About Section
                item {
                    SettingsSection(title = "About") {
                        SettingsButton(
                            title = "Privacy Policy",
                            icon = R.drawable.outline_shield_24
                        ) {
                            navController.navigate("privacy")
                        }
                        SettingsButton(
                            title = "Terms of Service",
                            icon = R.drawable.outline_description_24
                        ) {
                            navController.navigate("terms")
                        }
                        SettingsButton(
                            title = "Report an Issue",
                            icon = R.drawable.outline_bug_report_24
                        ) {
                            navController.navigate("report")
                        }
                        SettingsButton(
                            title = "App Version",
                            description = "1.0.0",
                            icon = R.drawable.outline_info_24
                        ) {}
                    }
                }

                // Add bottom padding
                item { Spacer(modifier = Modifier.height(16.dp)) }
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
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0077B6)
        )
        Spacer(modifier = Modifier.height(16.dp))
        content()
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
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color(0xFF0077B6),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = Color(0xFF2D2D2D)
                )
                if (description != null) {
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0077B6),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }
}

@Composable
fun SettingsButton(
    title: String,
    description: String? = null,
    icon: Int,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color(0xFF0077B6),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = Color(0xFF2D2D2D)
                )
                if (description != null) {
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
        }
        Icon(
            painter = painterResource(R.drawable.outline_chevron_right_24),
            contentDescription = null,
            tint = Color(0xFF757575),
            modifier = Modifier.size(24.dp)
        )
    }
}
