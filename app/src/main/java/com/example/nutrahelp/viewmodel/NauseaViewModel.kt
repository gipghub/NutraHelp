package com.example.nutrahelp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrahelp.data.NauseaEntryEntity
import com.example.nutrahelp.data.NutraHelpDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NauseaViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = NutraHelpDatabase.getInstance(app).nauseaDao()

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: NauseaEntryEntity) = viewModelScope.launch { dao.insert(entry) }

    fun delete(entry: NauseaEntryEntity) = viewModelScope.launch { dao.delete(entry) }

    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}