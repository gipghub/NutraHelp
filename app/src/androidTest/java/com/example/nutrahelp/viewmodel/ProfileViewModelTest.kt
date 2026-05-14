package com.example.nutrahelp.viewmodel

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileViewModelTest {

    private val app = testApp()
    private val prefs = app.getSharedPreferences("profile", Context.MODE_PRIVATE)

    @Before
    fun clearPrefs() {
        prefs.edit().clear().commit()
    }

    @After
    fun teardown() {
        prefs.edit().clear().commit()
    }

    @Test
    fun initialState_usesDefaults() {
        val vm = ProfileViewModel(app)
        val s = vm.state.value
        assertEquals("", s.name)
        assertEquals("", s.age)
        assertEquals("Prefer not to say", s.sex)
        assertEquals("Lose Weight", s.primaryGoal)
        assertEquals("", s.heightCm)
        assertEquals("", s.currentWeight)
        assertEquals("", s.startingWeight)
        assertEquals("", s.goalWeight)
        assertEquals("Semaglutide (Ozempic)", s.medication)
        assertEquals("", s.dose)
        assertEquals("Monday", s.injectionDay)
        assertEquals("", s.startDate)
        assertEquals("", s.weeksOnMed)
        assertEquals("1600", s.calorieGoal)
        assertEquals("120", s.proteinGoal)
    }

    @Test
    fun save_updatesStateImmediately() {
        val vm = ProfileViewModel(app)
        val updated = ProfileState(
            name = "Jane", age = "34", sex = "Female", primaryGoal = "Maintain Weight",
            heightCm = "165", currentWeight = "70", startingWeight = "80", goalWeight = "65",
            medication = "Tirzepatide (Mounjaro)", dose = "2.5 mg", injectionDay = "Wednesday",
            startDate = "01/01/2024", weeksOnMed = "12",
            calorieGoal = "1800", proteinGoal = "140",
        )
        vm.save(updated)
        assertEquals(updated, vm.state.value)
    }

    @Test
    fun save_persistsThroughViewModelRecreation() {
        val vm1 = ProfileViewModel(app)
        vm1.save(ProfileState(
            name = "Jane", age = "34", sex = "Female", primaryGoal = "Build Muscle",
            heightCm = "165", currentWeight = "70", startingWeight = "80", goalWeight = "65",
            medication = "Tirzepatide (Mounjaro)", dose = "2.5 mg", injectionDay = "Friday",
            startDate = "03/15/2024", weeksOnMed = "8",
            calorieGoal = "2000", proteinGoal = "150",
        ))

        val vm2 = ProfileViewModel(app)
        val s = vm2.state.value
        assertEquals("Jane", s.name)
        assertEquals("34", s.age)
        assertEquals("Female", s.sex)
        assertEquals("Build Muscle", s.primaryGoal)
        assertEquals("165", s.heightCm)
        assertEquals("70", s.currentWeight)
        assertEquals("80", s.startingWeight)
        assertEquals("65", s.goalWeight)
        assertEquals("Tirzepatide (Mounjaro)", s.medication)
        assertEquals("2.5 mg", s.dose)
        assertEquals("Friday", s.injectionDay)
        assertEquals("03/15/2024", s.startDate)
        assertEquals("8", s.weeksOnMed)
        assertEquals("2000", s.calorieGoal)
        assertEquals("150", s.proteinGoal)
    }

    @Test
    fun save_twice_lastWriteWins() {
        val vm = ProfileViewModel(app)
        vm.save(ProfileState(name = "Alice", calorieGoal = "1500", proteinGoal = "100"))
        vm.save(ProfileState(name = "Bob", calorieGoal = "1800", proteinGoal = "130"))
        assertEquals("Bob", vm.state.value.name)
        assertEquals("1800", vm.state.value.calorieGoal)
        assertEquals("130", vm.state.value.proteinGoal)

        val vm2 = ProfileViewModel(app)
        assertEquals("Bob", vm2.state.value.name)
    }

    @Test
    fun save_emptyStrings_persistCorrectly() {
        val vm1 = ProfileViewModel(app)
        vm1.save(ProfileState(name = "Jane", heightCm = "165", currentWeight = "70"))

        val vm2 = ProfileViewModel(app)
        vm2.save(ProfileState())

        val vm3 = ProfileViewModel(app)
        assertEquals("", vm3.state.value.name)
        assertEquals("", vm3.state.value.heightCm)
        assertEquals("", vm3.state.value.currentWeight)
    }
}