package com.example.nutrahelp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrahelp.data.NutraHelpDatabase
import com.example.nutrahelp.data.WeightDao
import com.example.nutrahelp.data.WeightEntryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeightViewModel(
    app: Application,
    private val dao: WeightDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).weightDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: WeightEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: WeightEntryEntity) = viewModelScope.launch { dao.delete(entry) }
}