package com.example.nutrahelp.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nutrahelp.data.InjectionRecordEntity
import com.example.nutrahelp.data.NutraHelpDatabase
import com.example.nutrahelp.data.TitrationEntryEntity
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MedicationHistoryViewModelTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var vm: MedicationHistoryViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        db = buildTestDb()
        vm = MedicationHistoryViewModel(testApp(), db.titrationDao(), db.injectionDao())
    }

    @After
    fun teardown() {
        vm.viewModelScope.cancel()
        Dispatchers.resetMain()
        db.close()
    }

    @Test
    fun insertTitration_persistsData() = runTest(testDispatcher) {
        vm.insertTitration(TitrationEntryEntity(date = "05/12/2026", medication = "Ozempic", dose = "0.5 mg", reason = "Start", notes = ""))
        advanceUntilIdle()

        val entries = db.titrationDao().getAll().first()
        assertEquals(1, entries.size)
        assertEquals("0.5 mg", entries[0].dose)
    }

    @Test
    fun deleteTitration_removesData() = runTest(testDispatcher) {
        vm.insertTitration(TitrationEntryEntity(date = "05/12/2026", medication = "Ozempic", dose = "0.5 mg", reason = "Start", notes = ""))
        advanceUntilIdle()

        val inserted = db.titrationDao().getAll().first().first()
        vm.deleteTitration(inserted)
        advanceUntilIdle()

        assertTrue(db.titrationDao().getAll().first().isEmpty())
    }

    @Test
    fun insertInjection_persistsData() = runTest(testDispatcher) {
        vm.insertInjection(InjectionRecordEntity(date = "05/12/2026", medication = "Ozempic", dose = "0.5 mg", notes = "Left thigh"))
        advanceUntilIdle()

        val entries = db.injectionDao().getAll().first()
        assertEquals(1, entries.size)
        assertEquals("Left thigh", entries[0].notes)
    }

    @Test
    fun deleteInjection_removesData() = runTest(testDispatcher) {
        vm.insertInjection(InjectionRecordEntity(date = "05/12/2026", medication = "Ozempic", dose = "0.5 mg", notes = ""))
        advanceUntilIdle()

        val inserted = db.injectionDao().getAll().first().first()
        vm.deleteInjection(inserted)
        advanceUntilIdle()

        assertTrue(db.injectionDao().getAll().first().isEmpty())
    }

    @Test
    fun titrations_and_injections_independent() = runTest(testDispatcher) {
        vm.insertTitration(TitrationEntryEntity(date = "05/12/2026", medication = "Ozempic", dose = "0.5 mg", reason = "Start", notes = ""))
        vm.insertInjection(InjectionRecordEntity(date = "05/12/2026", medication = "Ozempic", dose = "0.5 mg", notes = ""))
        advanceUntilIdle()

        assertEquals(1, db.titrationDao().getAll().first().size)
        assertEquals(1, db.injectionDao().getAll().first().size)
    }
}
