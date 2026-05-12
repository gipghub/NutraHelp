package com.example.nutrahelp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrahelp.data.InjectionRecordEntity
import com.example.nutrahelp.data.NutraHelpDatabase
import com.example.nutrahelp.data.TitrationEntryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MedicationHistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val db = NutraHelpDatabase.getInstance(app)
    private val titrationDao = db.titrationDao()
    private val injectionDao = db.injectionDao()

    val titrations = titrationDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val injections = injectionDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertTitration(entry: TitrationEntryEntity) =
        viewModelScope.launch { titrationDao.insert(entry) }

    fun deleteTitration(entry: TitrationEntryEntity) =
        viewModelScope.launch { titrationDao.delete(entry) }

    fun insertInjection(entry: InjectionRecordEntity) =
        viewModelScope.launch { injectionDao.insert(entry) }

    fun deleteInjection(entry: InjectionRecordEntity) =
        viewModelScope.launch { injectionDao.delete(entry) }
}