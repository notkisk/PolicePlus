package com.example.policeplus.views.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.policeplus.R
import com.example.policeplus.ui.theme.InterFont
import com.example.policeplus.ui.theme.PolicePlusBlue

@Composable
fun RecentScanCard(
    name: String = "Haithem Bekkari",
    date: String = "Today 26 September 2025"
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { showDialog = true }, // ✅ Open popup on click
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = PolicePlusBlue),
        elevation = CardDefaults.elevatedCardElevation(5.dp) // ✅ Drop shadow
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.id_card),
                contentDescription = "ID Icon",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 4.dp)
            ) {
                Text(
                    text = date,
                    color = Color(0xFFBDBDBD),
                    fontFamily = InterFont,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.White, fontFamily = InterFont, fontSize = 20.sp)) {
                            append("Name: ") // ✅ Regular
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White, fontFamily = InterFont, fontSize = 20.sp)) {
                            append(name) // ✅ Bold
                        }
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    if (showDialog) {
        ScanDetailsPopup(name, onDismiss = { showDialog = false })
    }
}
