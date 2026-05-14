package com.example.nutrahelp.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nutrahelp.data.NauseaEntryEntity
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
class NauseaViewModelTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var vm: NauseaViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        db = buildTestDb()
        vm = NauseaViewModel(testApp(), db.nauseaDao())
    }

    @After
    fun teardown() {
        vm.viewModelScope.cancel()
        Dispatchers.resetMain()
        db.close()
    }

    private fun entry() = NauseaEntryEntity(
        dateTime = "May 12, 10:00 AM", timeOfDay = "Morning",
        severity = 1, triggers = "Stress", remedies = "Ginger",
        duration = "<30 min", notes = "",
    )

    @Test
    fun insert_persistsData() = runTest(testDispatcher) {
        vm.insert(entry())
        advanceUntilIdle()

        val entries = db.nauseaDao().getAll().first()
        assertEquals(1, entries.size)
        assertEquals("Stress", entries[0].triggers)
    }

    @Test
    fun delete_removesData() = runTest(testDispatcher) {
        vm.insert(entry())
        advanceUntilIdle()

        val inserted = db.nauseaDao().getAll().first().first()
        vm.delete(inserted)
        advanceUntilIdle()

        assertTrue(db.nauseaDao().getAll().first().isEmpty())
    }

    @Test
    fun deleteAll_clearsData() = runTest(testDispatcher) {
        repeat(3) { vm.insert(entry()) }
        advanceUntilIdle()

        assertEquals(3, db.nauseaDao().getAll().first().size)

        vm.deleteAll()
        advanceUntilIdle()

        assertTrue(db.nauseaDao().getAll().first().isEmpty())
    }
}
