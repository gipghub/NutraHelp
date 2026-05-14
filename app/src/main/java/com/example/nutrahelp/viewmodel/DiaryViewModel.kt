package com.example.nutrahelp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrahelp.data.DiaryDao
import com.example.nutrahelp.data.DiaryEntryEntity
import com.example.nutrahelp.data.NutraHelpDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiaryViewModel(
    app: Application,
    private val dao: DiaryDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).diaryDao())
    private val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val dateOffset = MutableStateFlow(0)

    private fun offsetToKey(offset: Int): String {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, offset) }
        return dateFmt.format(cal.time)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val entries = dateOffset
        .flatMapLatest { offset -> dao.getForDate(offsetToKey(offset)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setOffset(offset: Int) { dateOffset.value = offset }

    fun insert(entry: DiaryEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: DiaryEntryEntity) = viewModelScope.launch { dao.delete(entry) }

    fun dateKeyForOffset(offset: Int) = offsetToKey(offset)
}