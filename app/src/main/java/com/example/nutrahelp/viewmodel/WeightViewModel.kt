package com.example.nutrahelp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrahelp.data.NutraHelpDatabase
import com.example.nutrahelp.data.WeightDao
import com.example.nutrahelp.data.WeightEntryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val PREFS_NAME = "weight"
private const val KEY_GOAL = "goalWeight"

class WeightViewModel(
    app: Application,
    private val dao: WeightDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).weightDao())

    private val prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _goalWeight = MutableStateFlow(
        prefs.getFloat(KEY_GOAL, -1f).takeIf { it > 0f }
    )
    val goalWeight: StateFlow<Float?> = _goalWeight.asStateFlow()

    fun setGoalWeight(value: Float?) {
        _goalWeight.value = value
        if (value != null) prefs.edit().putFloat(KEY_GOAL, value).apply()
        else prefs.edit().remove(KEY_GOAL).apply()
    }

    fun insert(entry: WeightEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: WeightEntryEntity) = viewModelScope.launch { dao.delete(entry) }
}