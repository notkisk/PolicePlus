package com.example.policeplus.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.policeplus.ui.theme.Titles
import com.example.policeplus.views.components.RecentScanCard

@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {
    Box(modifier=Modifier.fillMaxSize().padding(16.dp)){

        Column(horizontalAlignment = Alignment.CenterHorizontally,verticalArrangement = Arrangement.Center, modifier = Modifier.padding(top = 20.dp)) {
            Text(text = "History", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Titles)
            Spacer(Modifier.height(50.dp))
            LazyColumn{items(12){
                RecentScanCard()
                Spacer(Modifier.height(16.dp))
            }
            }

        }
    }
}



