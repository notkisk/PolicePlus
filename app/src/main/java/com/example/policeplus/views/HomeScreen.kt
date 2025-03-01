package com.example.policeplus.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.policeplus.R
import com.example.policeplus.ui.theme.InterFont
import com.example.policeplus.ui.theme.PolicePlusBlue
import com.example.policeplus.ui.theme.Titles
import com.example.policeplus.views.components.RecentScanCard

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    var licensePlate by remember { mutableStateOf("") }
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Logo
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "PolicePlus Logo",
                modifier = Modifier
                    .size(138.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Welcome Message
            Text(
                text = "Welcome, Officer Ali! 👋",
                fontFamily = InterFont,
                fontSize = 20.sp, color = Titles, fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = licensePlate,
                onValueChange = {licensePlate=it},
                placeholder = { Text("Enter car License Plate", fontFamily = InterFont, color = Color(0xFFABABAB)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Search Icon",modifier=Modifier.size(20.dp), tint = Color(0xFF7C7C7C)
                    )
                }, shape = RoundedCornerShape(12.dp),   colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White, // Background when focused
                    unfocusedContainerColor = Color.White, // Background when not focused
                    disabledContainerColor = Color.Gray, // Background when disabled

                    focusedIndicatorColor = PolicePlusBlue, // Border color when focused
                    unfocusedIndicatorColor = Color(0xFFECECEC), // Border color when not focused
                    disabledIndicatorColor = Color.LightGray, // Border color when disabled


                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Scans
            Text("Recent Scans:", fontSize = 22.sp,fontFamily = InterFont, fontWeight = FontWeight.Bold, color = Titles)

            Spacer(modifier = Modifier.height(27.dp))


                RecentScanCard()
                Spacer(modifier = Modifier.height(27.dp))

                RecentScanCard("Drihem Abdelmoumen Abdelmoumen")
                Spacer(modifier = Modifier.height(27.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // Total Scanned Cars
            Row (verticalAlignment = Alignment.CenterVertically) {
                Text("Total Scanned Cars:", fontSize = 16.sp,fontFamily = InterFont, fontWeight = FontWeight.Bold, color = Titles)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(PolicePlusBlue, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("100", color = Color.White, fontFamily = InterFont, fontWeight = FontWeight.Bold)
                }
            }
        }
    }


