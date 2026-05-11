package com.example.nutrahelp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nutrahelp.ui.GroceryListScreen
import com.example.nutrahelp.ui.HomeScreen
import com.example.nutrahelp.ui.LogScreen
import com.example.nutrahelp.ui.MealPlanScreen
import com.example.nutrahelp.ui.ProfileScreen
import com.example.nutrahelp.ui.ProgressScreen
import com.example.nutrahelp.ui.SettingsScreen
import com.example.nutrahelp.ui.TipsScreen
import com.example.nutrahelp.ui.theme.NutraHelpTheme

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutraHelpTheme {
                val navController = rememberNavController()
                val navItems = listOf(
                    NavItem("home",     "Home",     Icons.Default.Home),
                    NavItem("log",      "Log",      Icons.Default.NoteAdd),
                    NavItem("meals",    "Meals",    Icons.Default.Restaurant),
                    NavItem("progress", "Progress", Icons.Default.TrendingDown),
                    NavItem("tips",     "Tips",     Icons.Default.Lightbulb),
                )
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            navItems.forEach { item ->
                                NavigationBarItem(
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                    label = { Text(item.label) },
                                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home")     {
                            HomeScreen(
                                onNavigateToProfile = { navController.navigate("profile") },
                                onNavigateToSettings = { navController.navigate("settings") }
                            )
                        }
                        composable("log")      { LogScreen() }
                        composable("meals")    {
                            MealPlanScreen(onNavigateToGrocery = { navController.navigate("grocery") })
                        }
                        composable("progress") { ProgressScreen() }
                        composable("tips")     { TipsScreen() }
                        composable("grocery")  {
                            GroceryListScreen(onBack = { navController.popBackStack() })
                        }
                        composable("profile")  {
                            ProfileScreen(onBack = { navController.popBackStack() })
                        }
                        composable("settings") {
                            SettingsScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
