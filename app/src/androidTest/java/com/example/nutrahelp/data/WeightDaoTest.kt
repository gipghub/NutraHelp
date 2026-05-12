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
class WeightDaoTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var dao: WeightDao

    @Before
    fun setup() {
        db = buildTestDb()
        dao = db.weightDao()
    }

    @After
    fun teardown() = db.close()

    @Test
    fun insertAndRetrieve() = runBlocking {
        val entry = WeightEntryEntity(date = "2026-05-12", weight = 185.5f, unit = "lbs")
        dao.insert(entry)

        val all = dao.getAll().first()
        assertEquals(1, all.size)
        assertEquals(185.5f, all[0].weight)
        assertEquals("lbs", all[0].unit)
        assertEquals("2026-05-12", all[0].date)
    }

    @Test
    fun multipleInserts_returnedNewestFirst() = runBlocking {
        dao.insert(WeightEntryEntity(id = 1L, date = "2026-05-10", weight = 186f, unit = "lbs"))
        dao.insert(WeightEntryEntity(id = 2L, date = "2026-05-11", weight = 185f, unit = "lbs"))
        dao.insert(WeightEntryEntity(id = 3L, date = "2026-05-12", weight = 184f, unit = "lbs"))

        val all = dao.getAll().first()
        assertEquals(3, all.size)
        // ORDER BY id DESC → id=3 first
        assertEquals(3L, all[0].id)
        assertEquals(1L, all[2].id)
    }

    @Test
    fun delete_removesEntry() = runBlocking {
        val entry = WeightEntryEntity(date = "2026-05-12", weight = 185f, unit = "lbs")
        dao.insert(entry)
        val inserted = dao.getAll().first().first()
        dao.delete(inserted)

        assertTrue(dao.getAll().first().isEmpty())
    }

    @Test
    fun deleteAll_clearsTable() = runBlocking {
        repeat(3) { i ->
            dao.insert(WeightEntryEntity(date = "2026-05-1${i + 1}", weight = 180f + i, unit = "kg"))
        }
        dao.deleteAll()

        assertTrue(dao.getAll().first().isEmpty())
    }

    @Test
    fun replaceOnConflict_updatesExistingEntry() = runBlocking {
        val original = WeightEntryEntity(id = 42L, date = "2026-05-12", weight = 185f, unit = "lbs")
        dao.insert(original)
        val updated = original.copy(weight = 183f, unit = "kg")
        dao.insert(updated)

        val all = dao.getAll().first()
        assertEquals(1, all.size)
        assertEquals(183f, all[0].weight)
        assertEquals("kg", all[0].unit)
    }
}