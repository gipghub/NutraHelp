package com.example.nutrahelp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrahelp.data.InjectionSiteDao
import com.example.nutrahelp.data.InjectionSiteEntryEntity
import com.example.nutrahelp.data.NutraHelpDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InjectionSiteViewModel(
    app: Application,
    private val dao: InjectionSiteDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).injectionSiteDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: InjectionSiteEntryEntity) = viewModelScope.launch { dao.insert(entry) }

    fun delete(entry: InjectionSiteEntryEntity) = viewModelScope.launch { dao.delete(entry) }

    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}