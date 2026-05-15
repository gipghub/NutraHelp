package com.example.nutrahelp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_entries")
data class WeightEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val weight: Float,
    val unit: String,
)

@Entity(tableName = "water_entries")
data class WaterEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,       // "yyyy-MM-dd" for grouping
    val time: String,       // display time "h:mm a"
    val amountMl: Int,
)

@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val dateOffset: Int,    // 0=today, -1=yesterday, etc.
    val date: String,       // "yyyy-MM-dd" for querying
    val time: String,
    val mealType: String,
    val foods: String,
    val calories: Int,      // 0 = not entered
    val protein: Int,       // 0 = not entered
    val hungerBefore: Int,
    val fullnessAfter: Int,
    val notes: String,
)

@Entity(tableName = "titration_entries")
data class TitrationEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val medication: String,
    val dose: String,
    val reason: String,
    val notes: String,
)

@Entity(tableName = "injection_records")
data class InjectionRecordEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val medication: String,
    val dose: String,
    val notes: String,
)

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val time: String,
    val provider: String,
    val type: String,
    val location: String,
    val notes: String,
    val completed: Boolean,
)

@Entity(tableName = "nausea_entries")
data class NauseaEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val dateTime: String,
    val timeOfDay: String,
    val severity: Int,
    val triggers: String,   // comma-separated
    val remedies: String,   // comma-separated
    val duration: String,
    val notes: String,
)

@Entity(tableName = "injection_site_entries")
data class InjectionSiteEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val dateTime: String,
    val timestampMs: Long,
    val site: String,
    val dose: String,
    val notes: String,
)

@Entity(tableName = "craving_entries")
data class CravingEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val time: String,
    val foodName: String,
    val intensity: Int,
    val trigger: String,
    val outcome: String,
    val notes: String,
)

@Entity(tableName = "sleep_entries")
data class SleepEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val bedtime: String,
    val wakeTime: String,
    val hoursSlept: Float,
    val quality: Int,
    val notes: String,
)

@Entity(tableName = "mood_entries")
data class MoodEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val time: String,
    val moodLevel: Int,
    val emotions: String,  // comma-separated
    val notes: String,
)

@Entity(tableName = "energy_entries")
data class EnergyEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val timeOfDay: String,
    val energyLevel: Int,
    val notes: String,
)

@Entity(tableName = "gut_entries")
data class GutEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val timeOfDay: String,
    val symptoms: String,  // comma-separated
    val severity: Int,
    val notes: String,
)

@Entity(tableName = "inflammation_entries")
data class InflammationEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val symptoms: String,  // comma-separated
    val severity: Int,
    val notes: String,
)

@Entity(tableName = "sensitivity_entries")
data class SensitivityEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val food: String,
    val severity: Int,
    val symptoms: String,  // comma-separated
    val notes: String,
)

@Entity(tableName = "blood_sugar_entries")
data class BloodSugarEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val readingType: String,
    val valueMgDl: Float,
    val notes: String,
)

@Entity(tableName = "bp_entries")
data class BpEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,  // -1 = not entered
    val notes: String,
)

@Entity(tableName = "stress_entries")
data class StressEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val level: Int,
    val triggers: String,  // comma-separated
    val notes: String,
)

@Entity(tableName = "exercise_entries")
data class ExerciseEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val type: String,
    val durationMins: Int,
    val intensity: String,
    val notes: String,
)

@Entity(tableName = "measurement_entries")
data class MeasurementEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val unit: String,
    val waist: Float,
    val hips: Float,
    val chest: Float,
    val leftArm: Float,
    val rightArm: Float,
    val leftThigh: Float,
    val rightThigh: Float,
)

@Entity(tableName = "cholesterol_entries")
data class CholesterolEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val total: Float,        // -1f = not entered
    val hdl: Float,          // -1f = not entered
    val ldl: Float,          // -1f = not entered
    val triglycerides: Float, // -1f = not entered
    val notes: String,
)

@Entity(tableName = "heart_rate_entries")
data class HeartRateEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val bpm: Int,
    val context: String,
    val notes: String,
)

@Entity(tableName = "a1c_entries")
data class A1CEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val value: Float,
    val notes: String,
)

@Entity(tableName = "step_entries")
data class StepEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val steps: Int,
    val goalMet: Boolean,
)

@Entity(tableName = "body_fat_entries")
data class BodyFatEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val bodyFatPercent: Float,
    val method: String,
)

@Entity(tableName = "fiber_entries")
data class FiberEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val foodName: String,
    val fiberG: Float,
)

@Entity(tableName = "alcohol_entries")
data class AlcoholEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val drinkName: String,
    val calories: Int,
)

@Entity(tableName = "caffeine_entries")
data class CaffeineEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val drinkName: String,
    val caffeineMg: Int,
    val time: String,
)

@Entity(tableName = "sugar_entries")
data class SugarEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val foodName: String,
    val sugarG: Int,
)

@Entity(tableName = "sodium_entries")
data class SodiumEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val foodName: String,
    val sodiumMg: Int,
)

@Entity(tableName = "macro_entries")
data class MacroEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val foodName: String,
    val carbsG: Float,
    val proteinG: Float,
    val fatG: Float,
)

@Entity(tableName = "protein_entries")
data class ProteinEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val foodName: String,
    val proteinG: Float,
)

@Entity(tableName = "calorie_entries")
data class CalorieEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val foodName: String,
    val calories: Int,
)

@Entity(tableName = "nutrient_entries")
data class NutrientEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val nutrientName: String,
    val amount: Float,
    val unit: String,
    val source: String,
)

@Entity(tableName = "hunger_entries")
data class HungerEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val time: String,
    val mealName: String,
    val hungerBefore: Int,
    val fullnessAfter: Int,
)

@Entity(tableName = "meal_timing_entries")
data class MealTimingEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val mealType: String,
    val mealName: String,
    val time: String,
    val minutesSinceMidnight: Int,
)

@Entity(tableName = "mindful_meal_entries")
data class MindfulMealEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val mealType: String,
    val hungerBefore: Int,
    val fullnessAfter: Int,
    val eatingMins: String,
    val distractions: String,  // comma-separated
    val notes: String,
)

@Entity(tableName = "milestone_entries")
data class MilestoneEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val category: String,
    val title: String,
    val description: String,
)

@Entity(tableName = "nsv_entries")
data class NsvEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val category: String,
    val description: String,
)

@Entity(tableName = "side_effect_entries")
data class SideEffectEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val effect: String,
    val severity: Int,
    val notes: String,
)

@Entity(tableName = "lab_entries")
data class LabEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val labTypeName: String,
    val labTypeUnit: String,
    val labTypeReference: String,
    val value: String,
    val notes: String,
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val mood: String,
    val energy: Int,
    val note: String,
)

@Entity(tableName = "checkin_entries")
data class CheckInEntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val time: String,
    val mood: Int,
    val energy: Int,
    val hunger: Int,
    val sleep: Int,
    val notes: String,
)

@Entity(tableName = "glp1_entries")
data class Glp1EntryEntity(
    @PrimaryKey val id: Long = System.nanoTime(),
    val date: String,
    val medication: String,
    val dose: String,
    val site: String,
    val notes: String,
)