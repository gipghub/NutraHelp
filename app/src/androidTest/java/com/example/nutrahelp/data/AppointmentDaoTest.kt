package com.example.nutrahelp.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppointmentDaoTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var dao: AppointmentDao

    @Before
    fun setup() {
        db = buildTestDb()
        dao = db.appointmentDao()
    }

    @After
    fun teardown() = db.close()

    private fun appt(
        provider: String = "Dr. Smith",
        type: String = "Endocrinologist",
        date: String = "05/12/2026",
        completed: Boolean = false,
    ) = AppointmentEntity(
        date = date,
        time = "10:00 AM",
        provider = provider,
        type = type,
        location = "Office",
        notes = "",
        completed = completed,
    )

    @Test
    fun insertAndRetrieve() = runBlocking {
        dao.insert(appt(provider = "Dr. Smith"))

        val all = dao.getAll().first()
        assertEquals(1, all.size)
        assertEquals("Dr. Smith", all[0].provider)
        assertFalse(all[0].completed)
    }

    @Test
    fun multipleInserts_newestFirst() = runBlocking {
        dao.insert(appt(provider = "Dr. A").copy(id = 1L))
        dao.insert(appt(provider = "Dr. B").copy(id = 2L))
        dao.insert(appt(provider = "Dr. C").copy(id = 3L))

        val all = dao.getAll().first()
        assertEquals("Dr. C", all[0].provider)
        assertEquals("Dr. A", all[2].provider)
    }

    @Test
    fun delete_removesEntry() = runBlocking {
        dao.insert(appt())
        val inserted = dao.getAll().first().first()
        dao.delete(inserted)

        assertTrue(dao.getAll().first().isEmpty())
    }

    @Test
    fun update_togglesCompleted() = runBlocking {
        dao.insert(appt(completed = false))
        val inserted = dao.getAll().first().first()

        dao.update(inserted.copy(completed = true))

        val updated = dao.getAll().first().first()
        assertTrue(updated.completed)
    }

    @Test
    fun update_untoggleCompleted() = runBlocking {
        dao.insert(appt(completed = true))
        val inserted = dao.getAll().first().first()

        dao.update(inserted.copy(completed = false))

        val updated = dao.getAll().first().first()
        assertFalse(updated.completed)
    }

    @Test
    fun completedAndUpcomingCoexist() = runBlocking {
        dao.insert(appt(provider = "Upcoming Doc", completed = false))
        dao.insert(appt(provider = "Done Doc", completed = true))

        val all = dao.getAll().first()
        assertEquals(2, all.size)
        assertEquals(1, all.count { it.completed })
        assertEquals(1, all.count { !it.completed })
    }

    @Test
    fun update_preservesOtherFields() = runBlocking {
        val original = appt(provider = "Dr. Smith", type = "Cardiologist")
        dao.insert(original)
        val inserted = dao.getAll().first().first()
        dao.update(inserted.copy(completed = true))

        val updated = dao.getAll().first().first()
        assertEquals("Dr. Smith", updated.provider)
        assertEquals("Cardiologist", updated.type)
        assertEquals("10:00 AM", updated.time)
        assertTrue(updated.completed)
    }
}