package com.example.nutrahelp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrahelp.data.CravingDao
import com.example.nutrahelp.data.CravingEntryEntity
import com.example.nutrahelp.data.NutraHelpDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CravingViewModel(
    app: Application,
    private val dao: CravingDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).cravingDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: CravingEntryEntity) = viewModelScope.launch { dao.insert(entry) }

    fun delete(entry: CravingEntryEntity) = viewModelScope.launch { dao.delete(entry) }

    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}