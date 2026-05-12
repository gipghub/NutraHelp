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
class InjectionSiteDaoTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var dao: InjectionSiteDao

    @Before
    fun setup() {
        db = buildTestDb()
        dao = db.injectionSiteDao()
    }

    @After
    fun teardown() = db.close()

    private fun entry(
        site: String = "Abdomen — Left Upper",
        dose: String = "0.5 mg",
        notes: String = "",
        timestampMs: Long = 1_000_000L,
    ) = InjectionSiteEntryEntity(
        dateTime = "May 12, 10:00 AM",
        timestampMs = timestampMs,
        site = site,
        dose = dose,
        notes = notes,
    )

    @Test
    fun insertAndRetrieve() = runBlocking {
        dao.insert(entry(site = "Thigh — Left", dose = "0.25 mg"))

        val all = dao.getAll().first()
        assertEquals(1, all.size)
        assertEquals("Thigh — Left", all[0].site)
        assertEquals("0.25 mg", all[0].dose)
    }

    @Test
    fun multipleInserts_newestFirst() = runBlocking {
        dao.insert(entry(site = "Thigh — Left").copy(id = 10L))
        dao.insert(entry(site = "Thigh — Right").copy(id = 20L))
        dao.insert(entry(site = "Abdomen — Left Upper").copy(id = 30L))

        val all = dao.getAll().first()
        assertEquals(30L, all[0].id)
        assertEquals(10L, all[2].id)
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
    fun timestampStored() = runBlocking {
        dao.insert(entry(timestampMs = 9_999_999L))
        val e = dao.getAll().first().first()
        assertEquals(9_999_999L, e.timestampMs)
    }

    @Test
    fun multipleSitesTrackedIndependently() = runBlocking {
        dao.insert(entry(site = "Thigh — Left").copy(id = 1L))
        dao.insert(entry(site = "Thigh — Right").copy(id = 2L))
        dao.insert(entry(site = "Thigh — Left").copy(id = 3L))

        val all = dao.getAll().first()
        assertEquals(3, all.size)
        assertEquals(2, all.count { it.site == "Thigh — Left" })
        assertEquals(1, all.count { it.site == "Thigh — Right" })
    }
}