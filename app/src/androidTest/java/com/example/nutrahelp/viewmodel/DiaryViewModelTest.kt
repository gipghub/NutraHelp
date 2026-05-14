package com.example.nutrahelp.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nutrahelp.data.DiaryEntryEntity
import com.example.nutrahelp.data.NutraHelpDatabase
import com.example.nutrahelp.data.buildTestDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DiaryViewModelTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var vm: DiaryViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun dateFor(offset: Int): String {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, offset) }
        return dateFmt.format(cal.time)
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        db = buildTestDb()
        vm = DiaryViewModel(testApp(), db.diaryDao())
    }

    @After
    fun teardown() {
        vm.viewModelScope.cancel()
        Dispatchers.resetMain()
        db.close()
    }

    private fun entry(date: String, foods: String = "Oatmeal") = DiaryEntryEntity(
        dateOffset = 0, date = date, time = "8:00 AM", mealType = "Breakfast",
        foods = foods, calories = 300, protein = 10, hungerBefore = 3, fullnessAfter = 4, notes = "",
    )

    @Test
    fun initialOffset_isZero() {
        assertEquals(0, vm.dateOffset.value)
    }

    @Test
    fun setOffset_updatesDateOffset() {
        vm.setOffset(-1)
        assertEquals(-1, vm.dateOffset.value)
        vm.setOffset(0)
        assertEquals(0, vm.dateOffset.value)
    }

    @Test
    fun dateKeyForOffset_zero_returnsToday() {
        assertEquals(dateFor(0), vm.dateKeyForOffset(0))
    }

    @Test
    fun dateKeyForOffset_minusOne_returnsYesterday() {
        assertEquals(dateFor(-1), vm.dateKeyForOffset(-1))
    }

    @Test
    fun insert_persistsForDate() = runTest(testDispatcher) {
        val today = dateFor(0)
        vm.insert(entry(date = today, foods = "Eggs"))
        advanceUntilIdle()

        val entries = db.diaryDao().getForDate(today).first()
        assertEquals(1, entries.size)
        assertEquals("Eggs", entries[0].foods)
    }

    @Test
    fun insert_differentDates_queryIsolated() = runTest(testDispatcher) {
        val today = dateFor(0)
        val yesterday = dateFor(-1)

        vm.insert(entry(date = today, foods = "Today food"))
        vm.insert(entry(date = yesterday, foods = "Yesterday food"))
        advanceUntilIdle()

        assertEquals(1, db.diaryDao().getForDate(today).first().size)
        assertEquals("Today food", db.diaryDao().getForDate(today).first()[0].foods)
        assertEquals(1, db.diaryDao().getForDate(yesterday).first().size)
        assertEquals("Yesterday food", db.diaryDao().getForDate(yesterday).first()[0].foods)
    }

    @Test
    fun delete_removesEntry() = runTest(testDispatcher) {
        val today = dateFor(0)
        vm.insert(entry(date = today))
        advanceUntilIdle()

        val inserted = db.diaryDao().getForDate(today).first().first()
        vm.delete(inserted)
        advanceUntilIdle()

        assertTrue(db.diaryDao().getForDate(today).first().isEmpty())
    }
}
