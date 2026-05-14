package com.example.nutrahelp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrahelp.data.NutraHelpDatabase
import com.example.nutrahelp.data.WaterDao
import com.example.nutrahelp.data.WaterEntryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PREFS_NAME = "water"
private const val KEY_GOAL_ML = "goalMl"
private const val DEFAULT_GOAL_ML = 2000

class WaterViewModel(
    app: Application,
    private val dao: WaterDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).waterDao())

    private val prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val todayEntries = dao.getForDate(todayKey)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyTotals = dao.getWeeklyTotals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _goalMl = MutableStateFlow(prefs.getInt(KEY_GOAL_ML, DEFAULT_GOAL_ML))
    val goalMl: StateFlow<Int> = _goalMl.asStateFlow()

    fun setGoal(ml: Int) {
        _goalMl.value = ml
        prefs.edit().putInt(KEY_GOAL_ML, ml).apply()
    }

    fun insert(entry: WaterEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: WaterEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun resetToday() = viewModelScope.launch { dao.deleteForDate(todayKey) }
}