package com.example.policeplus.views

import Car
import CarViewModel
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.policeplus.ui.theme.PolicePlusBlue
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CarDataScreen(
    modifier: Modifier = Modifier,
    viewModel: CarViewModel = viewModel()
) {

    val car by viewModel.car.collectAsState()

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(50.dp))

        Text(
            "Car Data",
            color = Color(0xFF5B5B5B),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(30.dp))

        // Pass the entire Car object
        car?.let {
            ScanDetails(car = it)
        } ?: Text("No data available", color = Color.Gray)
    }
}






@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScanDetails(car: Car) {
    Box(Modifier.fillMaxSize().padding(top = 50.dp)) {
        Surface(
            color = PolicePlusBlue,
            shape = RoundedCornerShape(21.dp),
            modifier = Modifier.padding(24.dp).width(500.dp).height(450.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(16.dp)
            ) {
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

                val details = listOf(
                    "Owner:" to (car.owner ?: "Unknown"),
                    "License Plate:" to car.licenseNumber,
                    "Insurance Start:" to formatDate(car.insuranceStart),
                    "Insurance End:" to formatDate(car.insuranceEnd),
                    "Inspection Date:" to formatDate(car.inspectionStart),
                    "Inspection Period:" to formatDate(car.inspectionEnd),
                    "Tax Status:" to car.taxPaid,
                    "Stolen Car:" to  car.stolen_car
                )

                details.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(value, color = Color(0xFFE7E7E7), fontSize = 16.sp, textAlign = TextAlign.Start, modifier = Modifier.width(100.dp))
                    }
                }
            }
        }
    }
}
