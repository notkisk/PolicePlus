package com.example.policeplus.views

import com.example.policeplus.CarViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.policeplus.R
import com.example.policeplus.ui.theme.PolicePlusBlue


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val viewModel: CarViewModel = hiltViewModel()

    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            if (currentRoute != "scan") {  // ✅ Hide bottom bar on ScanScreen
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(viewModel, onSearch = { navController.navigate("data") }) }
            composable("data") { CarDataScreen(viewModel) }
            composable("scan") {
                ScanScreen(
                    onClose = { navController.popBackStack() }, // Go back
                    onConfirm = { navController.navigate("data") },
                    viewModel
                )
            }
            composable("profile") { ProfileScreen({}) }
            composable("history") { HistoryScreen(viewModel) }
        }
    }
}



@Composable
fun BottomNavigationBar(navController: NavController) {
    val navItemsList = listOf(
        NavItem("Home", painterResource(R.drawable.home), "home"),
        NavItem("Data", painterResource(R.drawable.details), "data"),
        NavItem("Scan", painterResource(R.drawable.scan), "scan"),
        NavItem("History", painterResource(R.drawable.history), "history"),
        NavItem("Profile", painterResource(R.drawable.profile), "profile")

    )

    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    Box(
        modifier = Modifier.fillMaxWidth().shadow(15.dp, shape = RectangleShape)
    ) {
        NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
            navItemsList.forEachIndexed { index, item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (item.route != currentRoute) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        if (index == 2) {
                            Box(modifier = Modifier.size(60.dp)) {
                                Icon(
                                    painter = painterResource(R.drawable.scan),
                                    contentDescription = item.label,
                                    modifier = Modifier.size(60.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        } else {
                            Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(24.dp))
                        }
                    },
                    label = { Text(item.label) },
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


data class NavItem(val label: String, val icon: Painter, val route: String)



