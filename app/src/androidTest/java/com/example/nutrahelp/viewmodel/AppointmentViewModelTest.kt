package com.example.nutrahelp.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nutrahelp.data.AppointmentEntity
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AppointmentViewModelTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var vm: AppointmentViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        db = buildTestDb()
        vm = AppointmentViewModel(testApp(), db.appointmentDao())
    }

    @After
    fun teardown() {
        vm.viewModelScope.cancel()
        Dispatchers.resetMain()
        db.close()
    }

    private fun appt(completed: Boolean = false) = AppointmentEntity(
        date = "05/12/2026", time = "10:00 AM", provider = "Dr. Smith",
        type = "Endocrinologist", location = "Office", notes = "", completed = completed,
    )

    @Test
    fun insert_persistsData() = runTest(testDispatcher) {
        vm.insert(appt())
        advanceUntilIdle()

        val entries = db.appointmentDao().getAll().first { it.isNotEmpty() }
        assertEquals(1, entries.size)
        assertEquals("Dr. Smith", entries[0].provider)
        assertFalse(entries[0].completed)
    }

    @Test
    fun delete_removesData() = runTest(testDispatcher) {
        vm.insert(appt())

        val inserted = db.appointmentDao().getAll().first { it.isNotEmpty() }.first()
        vm.delete(inserted)

        assertTrue(db.appointmentDao().getAll().first { it.isEmpty() }.isEmpty())
    }

    @Test
    fun toggleComplete_flipsToTrue() = runTest(testDispatcher) {
        vm.insert(appt(completed = false))
        advanceUntilIdle()

        val inserted = db.appointmentDao().getAll().first { it.isNotEmpty() }.first()
        vm.toggleComplete(inserted)
        advanceUntilIdle()

        assertTrue(db.appointmentDao().getAll().first { it.isNotEmpty() }.first().completed)
    }

    @Test
    fun toggleComplete_flipsBackToFalse() = runTest(testDispatcher) {
        vm.insert(appt(completed = true))
        advanceUntilIdle()

        val inserted = db.appointmentDao().getAll().first { it.isNotEmpty() }.first()
        vm.toggleComplete(inserted)
        advanceUntilIdle()

        assertFalse(db.appointmentDao().getAll().first { it.isNotEmpty() }.first().completed)
    }
}
