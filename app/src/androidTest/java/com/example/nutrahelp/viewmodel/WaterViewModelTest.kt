package com.example.nutrahelp.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nutrahelp.data.NutraHelpDatabase
import com.example.nutrahelp.data.WaterEntryEntity
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
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class WaterViewModelTest {

    private val app = testApp()
    private val prefs = app.getSharedPreferences("water", Context.MODE_PRIVATE)
    private lateinit var db: NutraHelpDatabase
    private lateinit var vm: WaterViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    @Before
    fun setup() {
        prefs.edit().clear().commit()
        Dispatchers.setMain(testDispatcher)
        db = buildTestDb()
        vm = WaterViewModel(app, db.waterDao())
    }

    @After
    fun teardown() {
        vm.viewModelScope.cancel()
        Dispatchers.resetMain()
        db.close()
        prefs.edit().clear().commit()
    }

    @Test
    fun insert_persists_todayEntry() = runTest(testDispatcher) {
        vm.insert(WaterEntryEntity(date = todayKey, time = "8:00 AM", amountMl = 250))
        advanceUntilIdle()

        val entries = db.waterDao().getForDate(todayKey).first()
        assertEquals(1, entries.size)
        assertEquals(250, entries[0].amountMl)
    }

    @Test
    fun insert_otherDay_notInTodayQuery() = runTest(testDispatcher) {
        vm.insert(WaterEntryEntity(date = "2000-01-01", time = "8:00 AM", amountMl = 500))
        advanceUntilIdle()

        val todayEntries = db.waterDao().getForDate(todayKey).first()
        assertTrue(todayEntries.isEmpty())

        val pastEntries = db.waterDao().getForDate("2000-01-01").first()
        assertEquals(1, pastEntries.size)
    }

    @Test
    fun delete_removesEntry() = runTest(testDispatcher) {
        vm.insert(WaterEntryEntity(date = todayKey, time = "8:00 AM", amountMl = 250))
        advanceUntilIdle()

        val inserted = db.waterDao().getForDate(todayKey).first { it.isNotEmpty() }.first()
        vm.delete(inserted)
        advanceUntilIdle()

        assertTrue(db.waterDao().getForDate(todayKey).first().isEmpty())
    }

    @Test
    fun resetToday_clearsAllTodayEntries() = runTest(testDispatcher) {
        vm.insert(WaterEntryEntity(date = todayKey, time = "8:00 AM", amountMl = 250))
        vm.insert(WaterEntryEntity(date = todayKey, time = "9:00 AM", amountMl = 300))

        assertEquals(2, db.waterDao().getForDate(todayKey).first { it.size == 2 }.size)

        vm.resetToday()

        assertTrue(db.waterDao().getForDate(todayKey).first { it.isEmpty() }.isEmpty())
    }

    @Test
    fun weeklyTotals_reflectsInsertions() = runTest(testDispatcher) {
        vm.insert(WaterEntryEntity(date = todayKey, time = "8:00 AM", amountMl = 250))
        vm.insert(WaterEntryEntity(date = todayKey, time = "12:00 PM", amountMl = 350))

        val totals = db.waterDao().getWeeklyTotals().first { it.isNotEmpty() && it[0].total == 600 }
        assertEquals(1, totals.size)
        assertEquals(600, totals[0].total)
    }

    @Test
    fun goalMl_defaultIs2000() {
        assertEquals(2000, vm.goalMl.value)
    }

    @Test
    fun setGoal_updatesStateImmediately() {
        vm.setGoal(1500)
        assertEquals(1500, vm.goalMl.value)
    }

    @Test
    fun setGoal_persistsThroughViewModelRecreation() {
        vm.setGoal(3000)
        vm.viewModelScope.cancel()

        val vm2 = WaterViewModel(app, db.waterDao())
        assertEquals(3000, vm2.goalMl.value)
        vm2.viewModelScope.cancel()
    }

    @Test
    fun setGoal_lastWriteWins() {
        vm.setGoal(1000)
        vm.setGoal(2500)
        assertEquals(2500, vm.goalMl.value)

        val vm2 = WaterViewModel(app, db.waterDao())
        assertEquals(2500, vm2.goalMl.value)
        vm2.viewModelScope.cancel()
    }
}
