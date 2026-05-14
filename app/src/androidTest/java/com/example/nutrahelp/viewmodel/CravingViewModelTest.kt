package com.example.nutrahelp.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nutrahelp.data.CravingEntryEntity
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CravingViewModelTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var vm: CravingViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        db = buildTestDb()
        vm = CravingViewModel(testApp(), db.cravingDao())
    }

    @After
    fun teardown() {
        vm.viewModelScope.cancel()
        Dispatchers.resetMain()
        db.close()
    }

    private fun entry(foodName: String = "Chocolate", outcome: String = "Resisted") = CravingEntryEntity(
        time = "May 12, 3:00 PM", foodName = foodName,
        intensity = 2, trigger = "Stress", outcome = outcome, notes = "",
    )

    @Test
    fun insert_persistsData() = runTest(testDispatcher) {
        vm.insert(entry(foodName = "Ice cream", outcome = "Gave In"))

        val entries = db.cravingDao().getAll().first { it.isNotEmpty() }
        assertEquals(1, entries.size)
        assertEquals("Ice cream", entries[0].foodName)
        assertEquals("Gave In", entries[0].outcome)
    }

    @Test
    fun delete_removesData() = runTest(testDispatcher) {
        vm.insert(entry())
        advanceUntilIdle()

        val inserted = db.cravingDao().getAll().first().first()
        vm.delete(inserted)
        advanceUntilIdle()

        assertTrue(db.cravingDao().getAll().first().isEmpty())
    }

    @Test
    fun deleteAll_clearsData() = runTest(testDispatcher) {
        vm.insert(entry("Pizza"))
        vm.insert(entry("Chips"))
        vm.insert(entry("Cookies"))

        assertEquals(3, db.cravingDao().getAll().first { it.size == 3 }.size)

        vm.deleteAll()

        assertTrue(db.cravingDao().getAll().first { it.isEmpty() }.isEmpty())
    }
}
