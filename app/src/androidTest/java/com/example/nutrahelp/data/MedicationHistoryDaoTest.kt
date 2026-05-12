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
class MedicationHistoryDaoTest {

    private lateinit var db: NutraHelpDatabase
    private lateinit var titrationDao: TitrationDao
    private lateinit var injectionDao: InjectionDao

    @Before
    fun setup() {
        db = buildTestDb()
        titrationDao = db.titrationDao()
        injectionDao = db.injectionDao()
    }

    @After
    fun teardown() = db.close()

    // ── Titration tests ───────────────────────────────────────────────────────

    @Test
    fun titration_insertAndRetrieve() = runBlocking {
        titrationDao.insert(
            TitrationEntryEntity(
                date = "05/12/2026",
                medication = "Semaglutide (Ozempic)",
                dose = "0.5 mg",
                reason = "Starting dose",
                notes = "",
            )
        )
        val all = titrationDao.getAll().first()
        assertEquals(1, all.size)
        assertEquals("0.5 mg", all[0].dose)
        assertEquals("Starting dose", all[0].reason)
    }

    @Test
    fun titration_multipleInserts_newestFirst() = runBlocking {
        titrationDao.insert(TitrationEntryEntity(id = 1L, date = "05/01/2026", medication = "Ozempic", dose = "0.25 mg", reason = "Start", notes = ""))
        titrationDao.insert(TitrationEntryEntity(id = 2L, date = "05/08/2026", medication = "Ozempic", dose = "0.5 mg", reason = "Titration", notes = ""))

        val all = titrationDao.getAll().first()
        assertEquals(2L, all[0].id)
        assertEquals(1L, all[1].id)
    }

    @Test
    fun titration_delete() = runBlocking {
        titrationDao.insert(TitrationEntryEntity(date = "05/12/2026", medication = "Ozempic", dose = "0.5 mg", reason = "Start", notes = ""))
        val inserted = titrationDao.getAll().first().first()
        titrationDao.delete(inserted)

        assertTrue(titrationDao.getAll().first().isEmpty())
    }

    // ── Injection tests ───────────────────────────────────────────────────────

    @Test
    fun injection_insertAndRetrieve() = runBlocking {
        injectionDao.insert(
            InjectionRecordEntity(
                date = "05/12/2026",
                medication = "Semaglutide (Ozempic)",
                dose = "0.5 mg",
                notes = "Left thigh",
            )
        )
        val all = injectionDao.getAll().first()
        assertEquals(1, all.size)
        assertEquals("0.5 mg", all[0].dose)
        assertEquals("Left thigh", all[0].notes)
    }

    @Test
    fun injection_multipleInserts_newestFirst() = runBlocking {
        injectionDao.insert(InjectionRecordEntity(id = 10L, date = "05/05/2026", medication = "Ozempic", dose = "0.25 mg", notes = ""))
        injectionDao.insert(InjectionRecordEntity(id = 20L, date = "05/12/2026", medication = "Ozempic", dose = "0.5 mg", notes = ""))

        val all = injectionDao.getAll().first()
        assertEquals(20L, all[0].id)
    }

    @Test
    fun injection_delete() = runBlocking {
        injectionDao.insert(InjectionRecordEntity(date = "05/12/2026", medication = "Ozempic", dose = "0.5 mg", notes = ""))
        val inserted = injectionDao.getAll().first().first()
        injectionDao.delete(inserted)

        assertTrue(injectionDao.getAll().first().isEmpty())
    }

    @Test
    fun titrationAndInjection_independentTables() = runBlocking {
        titrationDao.insert(TitrationEntryEntity(date = "05/12/2026", medication = "Ozempic", dose = "0.5 mg", reason = "Start", notes = ""))
        injectionDao.insert(InjectionRecordEntity(date = "05/12/2026", medication = "Ozempic", dose = "0.5 mg", notes = ""))

        assertEquals(1, titrationDao.getAll().first().size)
        assertEquals(1, injectionDao.getAll().first().size)
    }
}
