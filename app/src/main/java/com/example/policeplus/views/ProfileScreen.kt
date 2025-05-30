package com.example.policeplus.views

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.policeplus.R
import com.example.policeplus.UserViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(userViewModel: UserViewModel, onLogout: () -> Unit, navController: NavController) {
    val localUser by userViewModel.localUser.observeAsState() // Observe the COMBINED local user
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    // Determine the user type and data source *early*
    when (localUser?.userType) {
        "police" -> {
            // Display police officer profile
            val user = localUser!! // Safe to use !! because we checked for null above
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(50.dp))
                Text(
                    "Profile",
                    color = Color(0xFF5B5B5B),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFFFFFF)),
                    verticalArrangement = Arrangement.Center
                ) {
                    items(1) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            colors = CardDefaults.cardColors(Color(0xFFFFFFFF))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = if (user.officerImage?.isBlank() == false) rememberAsyncImagePainter(
                                        user.officerImage
                                    ) else painterResource(R.drawable.user),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.Gray, CircleShape),
                                    contentScale = ContentScale.FillWidth
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Officer ${user.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(0xFFFFFFFF),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = user.badgeNumber,
                                        color = Color(0xFF3A86FF),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    user.rank?.let { ProfileStat(title = "Rank", value = it) }
                                    user.department?.let { ProfileStat(title = "Dept", value = it) }
                                }
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            colors = CardDefaults.cardColors(Color(0xFFFFFFFF))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Account Information",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                ProfileInfoRow(
                                    painterResource(R.drawable.outline_person_outline_24),
                                    "Name",
                                    user.name
                                )
                                ProfileInfoRow(
                                    painterResource(R.drawable.outline_email_24),
                                    "Email",
                                    user.email
                                )
                                ProfileInfoRow(
                                    painterResource(R.drawable.outline_badge_24),
                                    "Badge",
                                    user.badgeNumber
                                )
                                user.department?.let {
                                    ProfileInfoRow(
                                        painterResource(R.drawable.outline_home_24),
                                        "Department",
                                        it
                                    )
                                }
                                user.rank?.let {
                                    ProfileInfoRow(
                                        painterResource(R.drawable.outline_local_police_24),
                                        "Rank",
                                        it
                                    )
                                }
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            colors = CardDefaults.cardColors(Color(0xFFFFFFFF))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(12.dp))

                                SettingsItem(
                                    painterResource(R.drawable.outline_settings_24),
                                    "App Settings"
                                ) {
                                    navController.navigate("settings")
                                }

                                SettingsItem(
                                    painterResource(R.drawable.outline_notifications_24),
                                    "Notifications"
                                ) {
                                    val intent =
                                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                            putExtra(
                                                Settings.EXTRA_APP_PACKAGE,
                                                context.packageName
                                            )
                                        }
                                    context.startActivity(intent)
                                }

                                SettingsItem(
                                    painterResource(R.drawable.outline_bug_report_24),
                                    "Report Bug/Issue"
                                ) {
                                    val url = "https://github.com/notkisk/PolicePlus/issues"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }

                                SettingsItem(
                                    painterResource(R.drawable.outline_email_24),
                                    "Help & Support"
                                ) {
                                    // Navigate to a Help screen or external FAQ page
                                    val url = "https://github.com/notkisk/PolicePlus"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }

                                SettingsItem(
                                    painterResource(R.drawable.outline_shield_24),
                                    "About PolicePlus"
                                ) {
                                    navController.navigate("about") // Create a composable screen route called "about"
                                }

                            }
                        }

                        LogoutButton(onLogout)
                    }
                }
            }
        }
        "normal" -> {
            // Display normal user profile
            val user = localUser!! // Safe because of the when condition
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(50.dp))
                Text(
                    "Profile",
                    color = Color(0xFF5B5B5B),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFFFFFF)),
                    verticalArrangement = Arrangement.Center
                ) {
                    items(1) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            colors = CardDefaults.cardColors(Color(0xFFFFFFFF))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = if(user.officerImage?.isBlank() == false)rememberAsyncImagePainter(user.officerImage)else painterResource(R.drawable.user),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.Gray, CircleShape),
                                    contentScale = ContentScale.FillWidth
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "User ${user.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(0xFFFFFFFF),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    user.licenseNumber?.let { it1 ->
                                        Text(
                                            text = it1,
                                            color = Color(0xFF3A86FF),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                //HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                                //Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    //user.rank?.let { ProfileStat(title = "Rank", value = it) }
                                    //user.department?.let { ProfileStat(title = "Dept", value = it) }
                                }
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            colors = CardDefaults.cardColors(Color(0xFFFFFFFF))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Account Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                user.name.let { it1 ->
                                    ProfileInfoRow(painterResource(R.drawable.outline_person_outline_24), "Name",
                                        it1
                                    )
                                }
                                user.email.let { it1 ->
                                    ProfileInfoRow(painterResource(R.drawable.outline_email_24), "Email",
                                        it1.toString()
                                    )
                                }
                                user.licenseNumber.let { it1 ->
                                    ProfileInfoRow(painterResource(R.drawable.outline_badge_24), "License",
                                        it1.toString()
                                    )
                                }
//                                user.department?.let { ProfileInfoRow(painterResource(R.drawable.outline_home_24), "Department", it) }
//                                user.rank?.let { ProfileInfoRow(painterResource(R.drawable.outline_local_police_24), "Rank", it) }
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            colors = CardDefaults.cardColors(Color(0xFFFFFFFF))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(12.dp))

                                SettingsItem(painterResource(R.drawable.outline_settings_24), "App Settings") {
                                    navController.navigate("settings")
                                }

                                SettingsItem(painterResource(R.drawable.outline_notifications_24), "Notifications") {
                                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                    }
                                    context.startActivity(intent)
                                }

                                SettingsItem(painterResource(R.drawable.outline_bug_report_24), "Report Bug/Issue") {
                                    val url = "https://github.com/notkisk/PolicePlus/issues"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }

                                SettingsItem(painterResource(R.drawable.outline_email_24), "Help & Support") {
                                    // Navigate to a Help screen or external FAQ page
                                    val url = "https://github.com/notkisk/PolicePlus"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }

                                SettingsItem(painterResource(R.drawable.outline_shield_24), "About PolicePlus") {
                                    navController.navigate("about") // Create a composable screen route called "about"
                                }

                            }
                        }

                        LogoutButton(onLogout)
                    }
                }
            }

        }
        else -> {
            // Handle loading or error state (user is null or userType is unknown)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Settings Info") },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun LogoutButton(onLogout: () -> Unit) {
    Button(
        onClick = onLogout,
        colors = ButtonDefaults.buttonColors(Color.Red),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(40.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
            Icon(painter = painterResource(R.drawable.outline_logout_24), contentDescription = "logout",tint = Color.White)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Logout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        }
    }
}


// Component for displaying account details
@Composable
fun ProfileInfoRow(icon: Painter, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = Color(0xFF0077B6),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 14.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp)
        }
    }
    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)  // Separator

}

// Component for displaying settings options with a click action
@Composable
fun SettingsItem(icon: Painter, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = Color(0xFF0077B6),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 16.sp)
    }
    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)  // Separator

}


@Composable
fun ProfileStat(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = title, color = Color.Gray, fontSize = 14.sp)

    }

}
