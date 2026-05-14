package com.example.nutrahelp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileState(
    val name: String = "",
    val age: String = "",
    val sex: String = "Prefer not to say",
    val primaryGoal: String = "Lose Weight",
    val heightCm: String = "",
    val currentWeight: String = "",
    val startingWeight: String = "",
    val goalWeight: String = "",
    val medication: String = "Semaglutide (Ozempic)",
    val dose: String = "",
    val injectionDay: String = "Monday",
    val startDate: String = "",
    val weeksOnMed: String = "",
    val calorieGoal: String = "1600",
    val proteinGoal: String = "120",
)

class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("profile", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(load())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private fun load() = ProfileState(
        name = prefs.getString("name", "") ?: "",
        age = prefs.getString("age", "") ?: "",
        sex = prefs.getString("sex", "Prefer not to say") ?: "Prefer not to say",
        primaryGoal = prefs.getString("primaryGoal", "Lose Weight") ?: "Lose Weight",
        heightCm = prefs.getString("heightCm", "") ?: "",
        currentWeight = prefs.getString("currentWeight", "") ?: "",
        startingWeight = prefs.getString("startingWeight", "") ?: "",
        goalWeight = prefs.getString("goalWeight", "") ?: "",
        medication = prefs.getString("medication", "Semaglutide (Ozempic)") ?: "Semaglutide (Ozempic)",
        dose = prefs.getString("dose", "") ?: "",
        injectionDay = prefs.getString("injectionDay", "Monday") ?: "Monday",
        startDate = prefs.getString("startDate", "") ?: "",
        weeksOnMed = prefs.getString("weeksOnMed", "") ?: "",
        calorieGoal = prefs.getString("calorieGoal", "1600") ?: "1600",
        proteinGoal = prefs.getString("proteinGoal", "120") ?: "120",
    )

    fun save(s: ProfileState) {
        _state.value = s
        prefs.edit()
            .putString("name", s.name)
            .putString("age", s.age)
            .putString("sex", s.sex)
            .putString("primaryGoal", s.primaryGoal)
            .putString("heightCm", s.heightCm)
            .putString("currentWeight", s.currentWeight)
            .putString("startingWeight", s.startingWeight)
            .putString("goalWeight", s.goalWeight)
            .putString("medication", s.medication)
            .putString("dose", s.dose)
            .putString("injectionDay", s.injectionDay)
            .putString("startDate", s.startDate)
            .putString("weeksOnMed", s.weeksOnMed)
            .putString("calorieGoal", s.calorieGoal)
            .putString("proteinGoal", s.proteinGoal)
            .apply()
    }
}