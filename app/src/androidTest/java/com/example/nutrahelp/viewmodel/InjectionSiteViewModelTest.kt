package com.example.nutrahelp.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nutrahelp.data.InjectionSiteEntryEntity
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
class InjectionSiteViewModelTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var vm: InjectionSiteViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        db = buildTestDb()
        vm = InjectionSiteViewModel(testApp(), db.injectionSiteDao())
    }

    @After
    fun teardown() {
        vm.viewModelScope.cancel()
        Dispatchers.resetMain()
        db.close()
    }

    private fun entry(site: String = "Thigh — Left") = InjectionSiteEntryEntity(
        dateTime = "May 12, 10:00 AM", timestampMs = System.currentTimeMillis(),
        site = site, dose = "0.5 mg", notes = "",
    )

    @Test
    fun insert_persistsData() = runTest(testDispatcher) {
        vm.insert(entry(site = "Abdomen — Left Upper"))
        advanceUntilIdle()

        val entries = db.injectionSiteDao().getAll().first()
        assertEquals(1, entries.size)
        assertEquals("Abdomen — Left Upper", entries[0].site)
    }

    @Test
    fun delete_removesData() = runTest(testDispatcher) {
        vm.insert(entry())
        advanceUntilIdle()

        val inserted = db.injectionSiteDao().getAll().first().first()
        vm.delete(inserted)
        advanceUntilIdle()

        assertTrue(db.injectionSiteDao().getAll().first().isEmpty())
    }

    @Test
    fun deleteAll_clearsData() = runTest(testDispatcher) {
        vm.insert(entry("Thigh — Left"))
        vm.insert(entry("Thigh — Right"))
        advanceUntilIdle()

        assertEquals(2, db.injectionSiteDao().getAll().first().size)

        vm.deleteAll()
        advanceUntilIdle()

        assertTrue(db.injectionSiteDao().getAll().first().isEmpty())
    }
}
