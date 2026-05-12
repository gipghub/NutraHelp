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
class NauseaDaoTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var dao: NauseaDao

    @Before
    fun setup() {
        db = buildTestDb()
        dao = db.nauseaDao()
    }

    @After
    fun teardown() = db.close()

    private fun entry(
        severity: Int = 1,
        triggers: String = "Stress,After eating",
        remedies: String = "Ginger",
        duration: String = "<30 min",
        notes: String = "",
    ) = NauseaEntryEntity(
        dateTime = "May 12, 10:00 AM",
        timeOfDay = "Morning",
        severity = severity,
        triggers = triggers,
        remedies = remedies,
        duration = duration,
        notes = notes,
    )

    @Test
    fun insertAndRetrieve() = runBlocking {
        dao.insert(entry(severity = 2, triggers = "Stress"))

        val all = dao.getAll().first()
        assertEquals(1, all.size)
        assertEquals(2, all[0].severity)
        assertEquals("Stress", all[0].triggers)
    }

    @Test
    fun multipleInserts_newestFirst() = runBlocking {
        dao.insert(entry().copy(id = 1L))
        dao.insert(entry().copy(id = 2L))
        dao.insert(entry().copy(id = 3L))

        val all = dao.getAll().first()
        assertEquals(3L, all[0].id)
        assertEquals(1L, all[2].id)
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
        repeat(3) { dao.insert(entry()) }
        dao.deleteAll()

        assertTrue(dao.getAll().first().isEmpty())
    }

    @Test
    fun triggersAndRemediesStored() = runBlocking {
        dao.insert(entry(triggers = "Stress,Movement,Smell/Taste", remedies = "Ginger,Rest"))
        val e = dao.getAll().first().first()
        assertEquals("Stress,Movement,Smell/Taste", e.triggers)
        assertEquals("Ginger,Rest", e.remedies)
    }

    @Test
    fun emptyTriggersAndRemedies() = runBlocking {
        dao.insert(entry(triggers = "", remedies = ""))
        val e = dao.getAll().first().first()
        assertEquals("", e.triggers)
        assertEquals("", e.remedies)
    }

    @Test
    fun allFieldsPreserved() = runBlocking {
        dao.insert(entry(severity = 3, duration = ">2 hours", notes = "Bad episode"))
        val e = dao.getAll().first().first()
        assertEquals(3, e.severity)
        assertEquals(">2 hours", e.duration)
        assertEquals("Bad episode", e.notes)
        assertEquals("Morning", e.timeOfDay)
        assertEquals("May 12, 10:00 AM", e.dateTime)
    }
}