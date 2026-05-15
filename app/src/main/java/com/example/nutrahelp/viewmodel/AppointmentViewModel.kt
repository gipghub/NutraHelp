package com.example.nutrahelp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrahelp.data.AppointmentDao
import com.example.nutrahelp.data.AppointmentEntity
import com.example.nutrahelp.data.NutraHelpDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AppointmentViewModel(
    app: Application,
    private val dao: AppointmentDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).appointmentDao())

    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

    val appointments = dao.getAll()
        .map { list ->
            list.sortedWith(compareByDescending { runCatching { dateFormat.parse(it.date) }.getOrNull() })
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: AppointmentEntity) = viewModelScope.launch { dao.insert(entry) }

    fun delete(entry: AppointmentEntity) = viewModelScope.launch { dao.delete(entry) }

    fun toggleComplete(entry: AppointmentEntity) =
        viewModelScope.launch { dao.update(entry.copy(completed = !entry.completed)) }
}