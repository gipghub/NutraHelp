package com.example.nutrahelp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrahelp.data.NutraHelpDatabase
import com.example.nutrahelp.data.WaterEntryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WaterViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = NutraHelpDatabase.getInstance(app).waterDao()
    private val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val todayEntries = dao.getForDate(todayKey)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyTotals = dao.getWeeklyTotals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: WaterEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: WaterEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun resetToday() = viewModelScope.launch { dao.deleteForDate(todayKey) }
}