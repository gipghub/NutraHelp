package com.example.nutrahelp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrahelp.data.AppointmentEntity
import com.example.nutrahelp.data.NutraHelpDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppointmentViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = NutraHelpDatabase.getInstance(app).appointmentDao()

    val appointments = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: AppointmentEntity) = viewModelScope.launch { dao.insert(entry) }

    fun delete(entry: AppointmentEntity) = viewModelScope.launch { dao.delete(entry) }

    fun toggleComplete(entry: AppointmentEntity) =
        viewModelScope.launch { dao.update(entry.copy(completed = !entry.completed)) }
}