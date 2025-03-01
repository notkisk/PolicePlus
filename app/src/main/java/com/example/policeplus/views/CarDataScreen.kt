package com.example.policeplus.views

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.policeplus.R
import com.example.policeplus.ui.theme.PolicePlusBlue
import com.example.policeplus.viewmodels.ScanViewModel

@Composable
fun NotesScreen(modifier: Modifier = Modifier,scanViewModel: ScanViewModel= viewModel()) {
    val extractedText by scanViewModel.extractedText.observeAsState("")
   Column (Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
       Spacer(Modifier.height(50.dp))

       Text("Car Data", color = Color(0xFF5B5B5B), textAlign = TextAlign.Center,fontSize = 24.sp, fontWeight = FontWeight.Bold)
       Spacer(Modifier.height(30.dp))
       ScanDetails(
           name  = "Haithem Bekkari",
           carPlate = extractedText,
           insuranceStartDate="2024/10/08",
           insuranceEndDate="2025/10/08",
           inspectionDate="2024/10/08",
           inspectionPeriod="2025/10/08",
           taxStatus="Paid",
           stolenCar="No"
       )
   }

        }


@Composable
fun ScanDetails(name: String = "Haithem Bekkari ",
                     carPlate:String = "5846712139",
                     insuranceStartDate:String="2024/10/08",
                     insuranceEndDate:String="2025/10/08",
                     inspectionDate:String="2024/10/08",
                     inspectionPeriod:String="2025/10/08",
                     taxStatus:String="Paid",
                     stolenCar:String="No"
                     ) {
    Box(Modifier.fillMaxSize().padding(top = 50.dp)){


        Surface (
            color = PolicePlusBlue,
            shape = RoundedCornerShape(21.dp), modifier = Modifier.padding(24.dp).width(500.dp).height(450.dp))
        {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top, modifier = Modifier.padding(16.dp)
            ) {


                Spacer(modifier = Modifier.height(12.dp))

                val details = listOf(
                    "Owner:" to name,
                    "License Plate:" to carPlate,
                    "Insurance Start:" to insuranceStartDate,
                    "Insurance End:" to insuranceEndDate,
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
        }
    }


}


