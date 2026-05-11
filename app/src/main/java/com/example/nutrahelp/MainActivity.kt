package com.example.nutrahelp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Restaurant
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
import com.example.nutrahelp.ui.AppointmentTrackerScreen
import com.example.nutrahelp.ui.BloodPressureLogScreen
import com.example.nutrahelp.ui.BloodSugarLogScreen
import com.example.nutrahelp.ui.CholesterolLogScreen
import com.example.nutrahelp.ui.BodyMeasurementsScreen
import com.example.nutrahelp.ui.BmiStatsScreen
import com.example.nutrahelp.ui.DailyJournalScreen
import com.example.nutrahelp.ui.FastingTimerScreen
import com.example.nutrahelp.ui.FiberTrackerScreen
import com.example.nutrahelp.ui.Glp1InjectionLogScreen
import com.example.nutrahelp.ui.GoalTrackerScreen
import com.example.nutrahelp.ui.HabitTrackerScreen
import com.example.nutrahelp.ui.HeartRateLogScreen
import com.example.nutrahelp.ui.ExerciseLogScreen
import com.example.nutrahelp.ui.LabResultsScreen
import com.example.nutrahelp.ui.MacroTrackerScreen
import com.example.nutrahelp.ui.MealPrepPlannerScreen
import com.example.nutrahelp.ui.MedicationTrackerScreen
import com.example.nutrahelp.ui.ProfileScreen
import com.example.nutrahelp.ui.ProgressScreen
import com.example.nutrahelp.ui.RecipesScreen
import com.example.nutrahelp.ui.SettingsScreen
import com.example.nutrahelp.ui.SideEffectsScreen
import com.example.nutrahelp.ui.SleepTrackerScreen
import com.example.nutrahelp.ui.StressTrackerScreen
import com.example.nutrahelp.ui.SupplementTrackerScreen
import com.example.nutrahelp.ui.WaterIntakeLogScreen
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
                    NavItem("log",      "Log",      Icons.AutoMirrored.Filled.NoteAdd),
                    NavItem("meals",    "Meals",    Icons.Default.Restaurant),
                    NavItem("progress", "Progress", Icons.AutoMirrored.Filled.TrendingDown),
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
                                onNavigateToSettings = { navController.navigate("settings") },
                                onNavigateToFasting = { navController.navigate("fasting") },
                                onNavigateToHabits = { navController.navigate("habits") },
                                onNavigateToWater = { navController.navigate("water") }
                            )
                        }
                        composable("log")      {
                            LogScreen(
                                onNavigateToSideEffects = { navController.navigate("sideeffects") },
                                onNavigateToSupplements = { navController.navigate("supplements") },
                                onNavigateToJournal = { navController.navigate("journal") },
                                onNavigateToBloodSugar = { navController.navigate("bloodsugar") },
                                onNavigateToSleep = { navController.navigate("sleep") },
                                onNavigateToMacros = { navController.navigate("macros") },
                                onNavigateToStress = { navController.navigate("stress") },
                                onNavigateToGlp1 = { navController.navigate("glp1") },
                                onNavigateToFiber = { navController.navigate("fiber") }
                            )
                        }
                        composable("meals")    {
                            MealPlanScreen(
                                onNavigateToGrocery = { navController.navigate("grocery") },
                                onNavigateToRecipes = { navController.navigate("recipes") },
                                onNavigateToMealPrep = { navController.navigate("mealprep") }
                            )
                        }
                        composable("progress") {
                            ProgressScreen(
                                onNavigateToStats = { navController.navigate("bmi") },
                                onNavigateToExercise = { navController.navigate("exercise") },
                                onNavigateToLab = { navController.navigate("lab") },
                                onNavigateToGoals = { navController.navigate("goals") },
                                onNavigateToMeasurements = { navController.navigate("measurements") },
                                onNavigateToBloodPressure = { navController.navigate("bloodpressure") },
                                onNavigateToHeartRate = { navController.navigate("heartrate") },
                                onNavigateToCholesterol = { navController.navigate("cholesterol") }
                            )
                        }
                        composable("tips")     { TipsScreen() }
                        composable("grocery")  {
                            GroceryListScreen(onBack = { navController.popBackStack() })
                        }
                        composable("profile")  {
                            ProfileScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToMedication = { navController.navigate("medication") },
                                onNavigateToAppointments = { navController.navigate("appointments") }
                            )
                        }
                        composable("recipes")    { RecipesScreen(onBack = { navController.popBackStack() }) }
                        composable("medication") { MedicationTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("sideeffects") { SideEffectsScreen(onBack = { navController.popBackStack() }) }
                        composable("bmi")         { BmiStatsScreen(onBack = { navController.popBackStack() }) }
                        composable("exercise")    { ExerciseLogScreen(onBack = { navController.popBackStack() }) }
                        composable("lab")         { LabResultsScreen(onBack = { navController.popBackStack() }) }
                        composable("supplements") { SupplementTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("journal")      { DailyJournalScreen(onBack = { navController.popBackStack() }) }
                        composable("fasting")      { FastingTimerScreen(onBack = { navController.popBackStack() }) }
                        composable("bloodsugar")   { BloodSugarLogScreen(onBack = { navController.popBackStack() }) }
                        composable("goals")        { GoalTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("appointments") { AppointmentTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("settings")     { SettingsScreen(onBack = { navController.popBackStack() }) }
                        composable("measurements") { BodyMeasurementsScreen(onBack = { navController.popBackStack() }) }
                        composable("habits")       { HabitTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("sleep")        { SleepTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("mealprep")      { MealPrepPlannerScreen(onBack = { navController.popBackStack() }) }
                        composable("water")         { WaterIntakeLogScreen(onBack = { navController.popBackStack() }) }
                        composable("macros")        { MacroTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("bloodpressure") { BloodPressureLogScreen(onBack = { navController.popBackStack() }) }
                        composable("stress")       { StressTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("glp1")         { Glp1InjectionLogScreen(onBack = { navController.popBackStack() }) }
                        composable("fiber")        { FiberTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("heartrate")    { HeartRateLogScreen(onBack = { navController.popBackStack() }) }
                        composable("cholesterol")  { CholesterolLogScreen(onBack = { navController.popBackStack() }) }
                    }
                }
            }
        }
    }
}
