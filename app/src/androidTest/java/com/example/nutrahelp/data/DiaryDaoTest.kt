package com.example.nutrahelp.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiaryDaoTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var dao: DiaryDao

    @Before
    fun setup() {
        db = buildTestDb()
        dao = db.diaryDao()
    }

    @After
    fun teardown() = db.close()

    private fun entry(
        dateOffset: Int = 0,
        date: String = "2026-05-12",
        mealType: String = "Breakfast",
        foods: String = "Oatmeal",
        calories: Int = 300,
        protein: Int = 10,
    ) = DiaryEntryEntity(
        dateOffset = dateOffset,
        date = date,
        time = "8:00 AM",
        mealType = mealType,
        foods = foods,
        calories = calories,
        protein = protein,
        hungerBefore = 3,
        fullnessAfter = 4,
        notes = "",
    )

    @Test
    fun insertAndRetrieveForDate() = runBlocking {
        dao.insert(entry(date = "2026-05-12", foods = "Oatmeal"))
        dao.insert(entry(date = "2026-05-12", foods = "Eggs"))
        dao.insert(entry(date = "2026-05-11", foods = "Granola"))

        val today = dao.getForDate("2026-05-12").first()
        assertEquals(2, today.size)
        assertTrue(today.all { it.date == "2026-05-12" })
    }

    @Test
    fun getForDate_orderedByIdAscending() = runBlocking {
        dao.insert(entry(date = "2026-05-12", foods = "First meal").copy(id = 100L))
        dao.insert(entry(date = "2026-05-12", foods = "Second meal").copy(id = 200L))
        dao.insert(entry(date = "2026-05-12", foods = "Third meal").copy(id = 300L))

        val entries = dao.getForDate("2026-05-12").first()
        assertEquals("First meal", entries[0].foods)
        assertEquals("Third meal", entries[2].foods)
    }

    @Test
    fun delete_removesOnlyTargetEntry() = runBlocking {
        dao.insert(entry(date = "2026-05-12", foods = "Oatmeal"))
        dao.insert(entry(date = "2026-05-12", foods = "Eggs"))
        val toDelete = dao.getForDate("2026-05-12").first().first()
        dao.delete(toDelete)

        val remaining = dao.getForDate("2026-05-12").first()
        assertEquals(1, remaining.size)
    }

    @Test
    fun getForDate_emptyForUnloggedDate() = runBlocking {
        dao.insert(entry(date = "2026-05-11"))
        assertTrue(dao.getForDate("2026-05-12").first().isEmpty())
    }

    @Test
    fun caloriesAndProteinStored() = runBlocking {
        dao.insert(entry(calories = 450, protein = 35))
        val e = dao.getForDate("2026-05-12").first().first()
        assertEquals(450, e.calories)
        assertEquals(35, e.protein)
    }

    @Test
    fun hungerFullnessStored() = runBlocking {
        dao.insert(entry().copy(hungerBefore = 4, fullnessAfter = 2))
        val e = dao.getForDate("2026-05-12").first().first()
        assertEquals(4, e.hungerBefore)
        assertEquals(2, e.fullnessAfter)
    }
}