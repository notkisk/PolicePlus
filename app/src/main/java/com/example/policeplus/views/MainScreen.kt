package com.example.policeplus.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.policeplus.R
import com.example.policeplus.ui.theme.PolicePlusBlue

import com.example.policeplus.models.NavItem

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navItemsList = listOf(
        NavItem("Home", painterResource(R.drawable.home)),
        NavItem("Data", painterResource(R.drawable.details)),
        NavItem("Scan", painterResource(R.drawable.scan)),
        NavItem("Profile", painterResource(R.drawable.profile)),
        NavItem("History", painterResource(R.drawable.history))
    )



    var selectedIndex by remember { mutableIntStateOf(0) } // Default screen is Home
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,

        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth().shadow(15.dp, shape = RectangleShape)
            ) {
                NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                    navItemsList.forEachIndexed { index, item ->
                        if (index == 2) {
                            // Custom Add Button (Bigger + Colored)
                            NavigationBarItem(
                                selected = selectedIndex == index,
                                onClick = { selectedIndex = index },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)

                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.scan),
                                            contentDescription = item.label,
                                            modifier = Modifier.size(60.dp), // ✅ Bigger icon
                                            tint = Color.Unspecified
                                        )
                                    }
                                }, label ={ Text(item.label) } ,
                                colors = NavigationBarItemDefaults.colors(
                                    //selectedIconColor = PolicePlusBlue,
                                    selectedTextColor = PolicePlusBlue,
                                    indicatorColor = Color.Transparent
                                )

                            )
                        } else {
                            // Regular Navigation Item
                            NavigationBarItem(
                                selected = selectedIndex == index,
                                onClick = { selectedIndex = index },
                                icon = {
                                    Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(24.dp))
                                }, label ={  Text(item.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PolicePlusBlue,
                                    selectedTextColor = PolicePlusBlue,
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            onSelectedIndexChange = { newIndex -> selectedIndex = newIndex } // 👈 Update state
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int, onSelectedIndexChange: (Int) -> Unit) {
    when (selectedIndex) {
        0 -> HomeScreen(onSearch = {onSelectedIndexChange(1)})
        1 -> CarDataScreen()
        2 -> ScanScreen(onClose = { onSelectedIndexChange(0)  }, onConfirm = { onSelectedIndexChange(1) }) // 👈 Go back to HomeScreen (or any other)
        3 -> ProfileScreen()
        4 -> HistoryScreen()
    }
}


