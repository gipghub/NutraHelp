package com.example.nutrahelp.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nutrahelp.data.NutraHelpDatabase
import com.example.nutrahelp.data.WeightEntryEntity
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
class WeightViewModelTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var vm: WeightViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        db = buildTestDb()
        vm = WeightViewModel(testApp(), db.weightDao())
    }

    @After
    fun teardown() {
        vm.viewModelScope.cancel()
        Dispatchers.resetMain()
        db.close()
    }

    @Test
    fun initialState_isEmpty() {
        assertTrue(vm.entries.value.isEmpty())
    }

    @Test
    fun insert_persistsData() = runTest(testDispatcher) {
        vm.insert(WeightEntryEntity(date = "2026-05-12", weight = 185f, unit = "lbs"))
        advanceUntilIdle()

        val entries = db.weightDao().getAll().first()
        assertEquals(1, entries.size)
        assertEquals(185f, entries[0].weight)
        assertEquals("lbs", entries[0].unit)
    }

    @Test
    fun delete_removesData() = runTest(testDispatcher) {
        vm.insert(WeightEntryEntity(date = "2026-05-12", weight = 185f, unit = "lbs"))
        advanceUntilIdle()
        val inserted = db.weightDao().getAll().first().first()

        vm.delete(inserted)
        advanceUntilIdle()

        assertTrue(db.weightDao().getAll().first().isEmpty())
    }

    @Test
    fun multipleInserts_newestFirst() = runTest(testDispatcher) {
        vm.insert(WeightEntryEntity(id = 1L, date = "2026-05-10", weight = 186f, unit = "lbs"))
        vm.insert(WeightEntryEntity(id = 2L, date = "2026-05-11", weight = 185f, unit = "lbs"))
        vm.insert(WeightEntryEntity(id = 3L, date = "2026-05-12", weight = 184f, unit = "lbs"))
        advanceUntilIdle()

        val entries = db.weightDao().getAll().first()
        assertEquals(3, entries.size)
        assertEquals(3L, entries[0].id)
        assertEquals(1L, entries[2].id)
    }
}
