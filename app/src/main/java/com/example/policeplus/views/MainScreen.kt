package com.example.policeplus.views

import RegisterScreen
import TicketDraftViewModel
import com.example.policeplus.CarViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.policeplus.R
import com.example.policeplus.UserViewModel
import com.example.policeplus.ui.theme.PolicePlusBlue



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val userViewModel: UserViewModel = hiltViewModel()
    val carViewModel: CarViewModel = hiltViewModel()
    val draftViewModel: TicketDraftViewModel = hiltViewModel()

    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }  // Holds login state
    val token by userViewModel.token.collectAsState()  // ✅ Properly collect the StateFlow

    LaunchedEffect(token) {
        isLoggedIn = !token.isNullOrEmpty()  // ✅ Update login state correctly
    }

    LaunchedEffect(userViewModel.token) {
        userViewModel.token.collect { token ->
            RetrofitInstance.authToken = token  // ✅ Ensure API always has latest token
        }
    }

    // Show loading until we check token
    if (isLoggedIn == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // If logged in, go to HomeScreen, else show LoginScreen
    val startDestination = if (isLoggedIn == true) "home" else "login"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            if (navController.currentBackStackEntryAsState().value?.destination?.route != "scan" && navController.currentBackStackEntryAsState().value?.destination?.route != "login"&&
                navController.currentBackStackEntryAsState().value?.destination?.route != "register") {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(carViewModel,onSearch = { navController.navigate("data") },navController,userViewModel,draftViewModel)  }
            composable("data") { CarDataScreen(carViewModel) }
            composable("scan") { ScanScreen({ navController.popBackStack() }, { navController.navigate("data") }, carViewModel) }
            composable("profile") { ProfileScreen(userViewModel, onLogout = {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }  // ✅ Clear backstack on logout
                }
                userViewModel.logout()
            },navController) }
            composable("history") { HistoryScreen(carViewModel,navController) }
            composable("register") { RegisterScreen(navController, userViewModel) }
            composable("login") { LoginScreen(navController, userViewModel) }
            composable("about") { AboutScreen({navController.popBackStack()}) }

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
        modifier = Modifier
            .fillMaxWidth()
            .shadow(15.dp, shape = RectangleShape)
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


