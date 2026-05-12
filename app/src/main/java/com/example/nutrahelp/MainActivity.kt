package com.example.nutrahelp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.doOnEnd
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.nutrahelp.ui.FoodSilhouetteBackground
import com.example.nutrahelp.ui.LoadingScreen
import com.example.nutrahelp.ui.LocalUseMetric
import com.example.nutrahelp.ui.OnboardingScreen
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
import com.example.nutrahelp.ui.A1CTrackerScreen
import com.example.nutrahelp.ui.AppointmentTrackerScreen
import com.example.nutrahelp.ui.BloodPressureLogScreen
import com.example.nutrahelp.ui.BloodSugarLogScreen
import com.example.nutrahelp.ui.CholesterolLogScreen
import com.example.nutrahelp.ui.BodyMeasurementsScreen
import com.example.nutrahelp.ui.BmiStatsScreen
import com.example.nutrahelp.ui.DailyJournalScreen
import com.example.nutrahelp.ui.FastingTimerScreen
import com.example.nutrahelp.ui.FiberTrackerScreen
import com.example.nutrahelp.ui.AlcoholTrackerScreen
import com.example.nutrahelp.ui.BodyFatTrackerScreen
import com.example.nutrahelp.ui.DailyCheckInScreen
import com.example.nutrahelp.ui.GutHealthLogScreen
import com.example.nutrahelp.ui.MilestoneLogScreen
import com.example.nutrahelp.ui.SodiumTrackerScreen
import com.example.nutrahelp.ui.EnergyLevelLogScreen
import com.example.nutrahelp.ui.MoodTrackerScreen
import com.example.nutrahelp.ui.VitaminMicronutrientLogScreen
import com.example.nutrahelp.ui.WeightLossProjectionScreen
import com.example.nutrahelp.ui.CalorieDeficitTrackerScreen
import com.example.nutrahelp.ui.FoodSensitivityLogScreen
import com.example.nutrahelp.ui.HungerFullnessLogScreen
import com.example.nutrahelp.ui.MealTimingLogScreen
import com.example.nutrahelp.ui.Glp1InjectionLogScreen
import com.example.nutrahelp.ui.NsvLogScreen
import com.example.nutrahelp.ui.ProteinSourceLogScreen
import com.example.nutrahelp.ui.GoalTrackerScreen
import com.example.nutrahelp.ui.HabitTrackerScreen
import com.example.nutrahelp.ui.HeartRateLogScreen
import com.example.nutrahelp.ui.ExerciseLogScreen
import com.example.nutrahelp.ui.LabResultsScreen
import com.example.nutrahelp.ui.MacroTrackerScreen
import com.example.nutrahelp.ui.MindfulEatingLogScreen
import com.example.nutrahelp.ui.MealPrepPlannerScreen
import com.example.nutrahelp.ui.MedicationTrackerScreen
import com.example.nutrahelp.ui.ProfileScreen
import com.example.nutrahelp.ui.ProgressScreen
import com.example.nutrahelp.ui.RecipesScreen
import com.example.nutrahelp.ui.SettingsScreen
import com.example.nutrahelp.ui.SideEffectsScreen
import com.example.nutrahelp.ui.SleepTrackerScreen
import com.example.nutrahelp.ui.StepCounterLogScreen
import com.example.nutrahelp.ui.StressTrackerScreen
import com.example.nutrahelp.ui.SupplementTrackerScreen
import com.example.nutrahelp.ui.WaterIntakeLogScreen
import com.example.nutrahelp.ui.SugarTrackerScreen
import com.example.nutrahelp.ui.CaffeineTrackerScreen
import com.example.nutrahelp.ui.RecipeNutritionCalculatorScreen
import com.example.nutrahelp.ui.InflammationLogScreen
import com.example.nutrahelp.ui.CravingLogScreen
import com.example.nutrahelp.ui.HydrationCalculatorScreen
import com.example.nutrahelp.ui.NauseaLogScreen
import com.example.nutrahelp.ui.InjectionSiteTrackerScreen
import com.example.nutrahelp.ui.MealLogScreen
import com.example.nutrahelp.ui.PortionSizeGuideScreen
import com.example.nutrahelp.ui.BmrTdeeCalculatorScreen
import com.example.nutrahelp.ui.TipsScreen
import com.example.nutrahelp.ui.theme.NutraHelpTheme

private data class NavItem(val route: String, val label: String, val icon: ImageVector)
private enum class AppScreen { LOADING, ONBOARDING, MAIN }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        splash.setOnExitAnimationListener { splashView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashView.view, View.TRANSLATION_Y, 0f, -splashView.view.height.toFloat()
            ).apply {
                duration = 450
                interpolator = AccelerateInterpolator()
            }
            val fadeOut = ObjectAnimator.ofFloat(splashView.view, View.ALPHA, 1f, 0f).apply {
                duration = 350
                startDelay = 100
            }
            AnimatorSet().apply {
                playTogether(slideUp, fadeOut)
                doOnEnd { splashView.remove() }
                start()
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = getSharedPreferences("nutrahelp_prefs", MODE_PRIVATE)
        val needsOnboarding = !prefs.getBoolean("onboarding_complete", false)
        setContent {
                var themePreference by remember { mutableStateOf("System") }
                var useMetric by remember { mutableStateOf(true) }
                val systemDark = isSystemInDarkTheme()
                val darkTheme = when (themePreference) {
                    "Dark" -> true
                    "Light" -> false
                    else -> systemDark
                }
            NutraHelpTheme(darkTheme = darkTheme) {
                var appScreen by remember { mutableStateOf(AppScreen.LOADING) }
                AnimatedContent(
                    targetState = appScreen,
                    transitionSpec = {
                        fadeIn(tween(400)) togetherWith fadeOut(tween(300))
                    },
                    label = "screen_transition",
                ) { screen ->
                when (screen) {
                    AppScreen.LOADING -> LoadingScreen(
                        onLoadingComplete = {
                            appScreen = if (needsOnboarding) AppScreen.ONBOARDING else AppScreen.MAIN
                        }
                    )
                    AppScreen.ONBOARDING -> OnboardingScreen(
                        onFinished = { name, goal ->
                            prefs.edit()
                                .putBoolean("onboarding_complete", true)
                                .putString("user_name", name)
                                .putString("primary_goal", goal)
                                .apply()
                            appScreen = AppScreen.MAIN
                        }
                    )
                    AppScreen.MAIN -> {
                val userName = remember { prefs.getString("user_name", "") ?: "" }
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
                    CompositionLocalProvider(LocalUseMetric provides useMetric) {
                    FoodSilhouetteBackground {
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home")     {
                            HomeScreen(
                                userName = userName,
                                onNavigateToProfile = { navController.navigate("profile") },
                                onNavigateToSettings = { navController.navigate("settings") },
                                onNavigateToFasting = { navController.navigate("fasting") },
                                onNavigateToHabits = { navController.navigate("habits") },
                                onNavigateToWater = { navController.navigate("water") },
                                onNavigateToDailyCheckIn = { navController.navigate("checkin") }
                            )
                        }
                        composable("log")      {
                            LogScreen(
                                onNavigateToMealLog = { navController.navigate("meallog") },
                                onNavigateToSideEffects = { navController.navigate("sideeffects") },
                                onNavigateToSupplements = { navController.navigate("supplements") },
                                onNavigateToJournal = { navController.navigate("journal") },
                                onNavigateToBloodSugar = { navController.navigate("bloodsugar") },
                                onNavigateToSleep = { navController.navigate("sleep") },
                                onNavigateToMacros = { navController.navigate("macros") },
                                onNavigateToStress = { navController.navigate("stress") },
                                onNavigateToGlp1 = { navController.navigate("glp1") },
                                onNavigateToFiber = { navController.navigate("fiber") },
                                onNavigateToFoodSensitivity = { navController.navigate("foodsensitivity") },
                                onNavigateToMindfulEating = { navController.navigate("mindfuleating") },
                                onNavigateToCalories = { navController.navigate("calories") },
                                onNavigateToProtein = { navController.navigate("protein") },
                                onNavigateToHunger = { navController.navigate("hunger") },
                                onNavigateToMealTiming = { navController.navigate("mealtiming") },
                                onNavigateToAlcohol = { navController.navigate("alcohol") },
                                onNavigateToMood = { navController.navigate("mood") },
                                onNavigateToEnergy = { navController.navigate("energy") },
                                onNavigateToVitamins = { navController.navigate("vitamins") },
                                onNavigateToSodium = { navController.navigate("sodium") },
                                onNavigateToGutHealth = { navController.navigate("guthealth") },
                                onNavigateToSugar = { navController.navigate("sugar") },
                                onNavigateToCaffeine = { navController.navigate("caffeine") },
                                onNavigateToInflammation = { navController.navigate("inflammation") },
                                onNavigateToCravings = { navController.navigate("cravings") },
                                onNavigateToHydration = { navController.navigate("hydration") },
                                onNavigateToNausea = { navController.navigate("nausea") },
                                onNavigateToInjectionSites = { navController.navigate("injectionsites") }
                            )
                        }
                        composable("meals")    {
                            MealPlanScreen(
                                onNavigateToGrocery = { navController.navigate("grocery") },
                                onNavigateToRecipes = { navController.navigate("recipes") },
                                onNavigateToMealPrep = { navController.navigate("mealprep") },
                                onNavigateToRecipeCalc = { navController.navigate("recipecalc") },
                                onNavigateToPortionGuide = { navController.navigate("portionguide") }
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
                                onNavigateToCholesterol = { navController.navigate("cholesterol") },
                                onNavigateToSteps = { navController.navigate("steps") },
                                onNavigateToA1C = { navController.navigate("a1c") },
                                onNavigateToNsv = { navController.navigate("nsv") },
                                onNavigateToBodyFat = { navController.navigate("bodyfat") },
                                onNavigateToProjection = { navController.navigate("projection") },
                                onNavigateToMilestones = { navController.navigate("milestones") },
                                onNavigateToBmrTdee = { navController.navigate("bmrtdee") }
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
                        composable("settings")     {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                useMetric = useMetric,
                                onUseMetricChange = { useMetric = it },
                                themePreference = themePreference,
                                onThemeChange = { themePreference = it }
                            )
                        }
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
                        composable("cholesterol")     { CholesterolLogScreen(onBack = { navController.popBackStack() }) }
                        composable("steps")           { StepCounterLogScreen(onBack = { navController.popBackStack() }) }
                        composable("a1c")             { A1CTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("foodsensitivity") { FoodSensitivityLogScreen(onBack = { navController.popBackStack() }) }
                        composable("mindfuleating")   { MindfulEatingLogScreen(onBack = { navController.popBackStack() }) }
                        composable("nsv")             { NsvLogScreen(onBack = { navController.popBackStack() }) }
                        composable("calories")        { CalorieDeficitTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("protein")         { ProteinSourceLogScreen(onBack = { navController.popBackStack() }) }
                        composable("hunger")          { HungerFullnessLogScreen(onBack = { navController.popBackStack() }) }
                        composable("mealtiming")      { MealTimingLogScreen(onBack = { navController.popBackStack() }) }
                        composable("alcohol")         { AlcoholTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("bodyfat")         { BodyFatTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("mood")            { MoodTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("energy")          { EnergyLevelLogScreen(onBack = { navController.popBackStack() }) }
                        composable("vitamins")        { VitaminMicronutrientLogScreen(onBack = { navController.popBackStack() }) }
                        composable("projection")      { WeightLossProjectionScreen(onBack = { navController.popBackStack() }) }
                        composable("checkin")         { DailyCheckInScreen(onBack = { navController.popBackStack() }) }
                        composable("sodium")          { SodiumTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("guthealth")       { GutHealthLogScreen(onBack = { navController.popBackStack() }) }
                        composable("milestones")      { MilestoneLogScreen(onBack = { navController.popBackStack() }) }
                        composable("sugar")           { SugarTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("caffeine")        { CaffeineTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("recipecalc")      { RecipeNutritionCalculatorScreen(onBack = { navController.popBackStack() }) }
                        composable("inflammation")    { InflammationLogScreen(onBack = { navController.popBackStack() }) }
                        composable("cravings")       { CravingLogScreen(onBack = { navController.popBackStack() }) }
                        composable("hydration")      { HydrationCalculatorScreen(onBack = { navController.popBackStack() }) }
                        composable("portionguide")   { PortionSizeGuideScreen(onBack = { navController.popBackStack() }) }
                        composable("bmrtdee")        { BmrTdeeCalculatorScreen(onBack = { navController.popBackStack() }) }
                        composable("nausea")         { NauseaLogScreen(onBack = { navController.popBackStack() }) }
                        composable("injectionsites") { InjectionSiteTrackerScreen(onBack = { navController.popBackStack() }) }
                        composable("meallog")        { MealLogScreen(onBack = { navController.popBackStack() }) }
                    }
                    } // end FoodSilhouetteBackground
                    } // end CompositionLocalProvider
                }
                } // end AppScreen.MAIN
                } // end when
                } // end AnimatedContent
            }
        }
    }
}
