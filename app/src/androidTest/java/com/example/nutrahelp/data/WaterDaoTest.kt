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
class WaterDaoTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var dao: WaterDao

    @Before
    fun setup() {
        db = buildTestDb()
        dao = db.waterDao()
    }

    @After
    fun teardown() = db.close()

    @Test
    fun insertAndRetrieveForDate() = runBlocking {
        dao.insert(WaterEntryEntity(date = "2026-05-12", time = "8:00 AM", amountMl = 250))
        dao.insert(WaterEntryEntity(date = "2026-05-12", time = "9:00 AM", amountMl = 300))
        dao.insert(WaterEntryEntity(date = "2026-05-11", time = "7:00 AM", amountMl = 200))

        val today = dao.getForDate("2026-05-12").first()
        assertEquals(2, today.size)
        assertTrue(today.all { it.date == "2026-05-12" })
    }

    @Test
    fun getForDate_emptyWhenNoEntriesForDate() = runBlocking {
        dao.insert(WaterEntryEntity(date = "2026-05-11", time = "8:00 AM", amountMl = 250))

        val entries = dao.getForDate("2026-05-12").first()
        assertTrue(entries.isEmpty())
    }

    @Test
    fun delete_removesOnlyTargetEntry() = runBlocking {
        dao.insert(WaterEntryEntity(date = "2026-05-12", time = "8:00 AM", amountMl = 250))
        dao.insert(WaterEntryEntity(date = "2026-05-12", time = "9:00 AM", amountMl = 300))
        val toDelete = dao.getForDate("2026-05-12").first().first()
        dao.delete(toDelete)

        val remaining = dao.getForDate("2026-05-12").first()
        assertEquals(1, remaining.size)
    }

    @Test
    fun deleteForDate_clearsOnlyThatDay() = runBlocking {
        dao.insert(WaterEntryEntity(date = "2026-05-12", time = "8:00 AM", amountMl = 250))
        dao.insert(WaterEntryEntity(date = "2026-05-12", time = "9:00 AM", amountMl = 300))
        dao.insert(WaterEntryEntity(date = "2026-05-11", time = "7:00 AM", amountMl = 200))
        dao.deleteForDate("2026-05-12")

        assertTrue(dao.getForDate("2026-05-12").first().isEmpty())
        assertEquals(1, dao.getForDate("2026-05-11").first().size)
    }

    @Test
    fun weeklyTotals_sumsPerDay() = runBlocking {
        dao.insert(WaterEntryEntity(date = "2026-05-10", time = "8:00 AM", amountMl = 250))
        dao.insert(WaterEntryEntity(date = "2026-05-10", time = "12:00 PM", amountMl = 350))
        dao.insert(WaterEntryEntity(date = "2026-05-11", time = "9:00 AM", amountMl = 500))

        val totals = dao.getWeeklyTotals().first()
        assertEquals(2, totals.size)
        val may11 = totals.first { it.date == "2026-05-11" }
        val may10 = totals.first { it.date == "2026-05-10" }
        assertEquals(500, may11.total)
        assertEquals(600, may10.total)
    }

    @Test
    fun weeklyTotals_limitsSeven() = runBlocking {
        repeat(10) { i ->
            dao.insert(WaterEntryEntity(date = "2026-05-%02d".format(i + 1), time = "8:00 AM", amountMl = 300))
        }
        val totals = dao.getWeeklyTotals().first()
        assertTrue(totals.size <= 7)
    }
}