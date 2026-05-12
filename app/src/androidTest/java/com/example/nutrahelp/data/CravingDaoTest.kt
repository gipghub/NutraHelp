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
class CravingDaoTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var dao: CravingDao

    @Before
    fun setup() {
        db = buildTestDb()
        dao = db.cravingDao()
    }

    @After
    fun teardown() = db.close()

    private fun entry(
        foodName: String = "Chocolate",
        intensity: Int = 2,
        trigger: String = "Stress",
        outcome: String = "Resisted",
        notes: String = "",
        time: String = "May 12, 3:00 PM",
    ) = CravingEntryEntity(
        time = time,
        foodName = foodName,
        intensity = intensity,
        trigger = trigger,
        outcome = outcome,
        notes = notes,
    )

    @Test
    fun insertAndRetrieve() = runBlocking {
        dao.insert(entry(foodName = "Ice cream", outcome = "Gave In"))

        val all = dao.getAll().first()
        assertEquals(1, all.size)
        assertEquals("Ice cream", all[0].foodName)
        assertEquals("Gave In", all[0].outcome)
    }

    @Test
    fun multipleInserts_newestFirst() = runBlocking {
        dao.insert(entry(foodName = "A").copy(id = 1L))
        dao.insert(entry(foodName = "B").copy(id = 2L))
        dao.insert(entry(foodName = "C").copy(id = 3L))

        val all = dao.getAll().first()
        assertEquals("C", all[0].foodName)
        assertEquals("A", all[2].foodName)
    }

    @Test
    fun delete_removesEntry() = runBlocking {
        dao.insert(entry())
        val inserted = dao.getAll().first().first()
        dao.delete(inserted)

        assertTrue(dao.getAll().first().isEmpty())
    }

    @Test
    fun deleteAll_clearsTable() = runBlocking {
        repeat(4) { dao.insert(entry()) }
        dao.deleteAll()

        assertTrue(dao.getAll().first().isEmpty())
    }

    @Test
    fun allFieldsPreserved() = runBlocking {
        dao.insert(entry(foodName = "Pizza", intensity = 4, trigger = "Boredom", outcome = "Partially", notes = "Late night", time = "May 12, 11:00 PM"))
        val e = dao.getAll().first().first()
        assertEquals("Pizza", e.foodName)
        assertEquals(4, e.intensity)
        assertEquals("Boredom", e.trigger)
        assertEquals("Partially", e.outcome)
        assertEquals("Late night", e.notes)
        assertEquals("May 12, 11:00 PM", e.time)
    }

    @Test
    fun mixedOutcomes_allStored() = runBlocking {
        dao.insert(entry(outcome = "Resisted"))
        dao.insert(entry(outcome = "Partially"))
        dao.insert(entry(outcome = "Gave In"))

        val all = dao.getAll().first()
        assertEquals(3, all.size)
        assertEquals(1, all.count { it.outcome == "Resisted" })
        assertEquals(1, all.count { it.outcome == "Partially" })
        assertEquals(1, all.count { it.outcome == "Gave In" })
    }
}