package com.example.nutrahelp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WeightEntryEntity::class,
        WaterEntryEntity::class,
        DiaryEntryEntity::class,
        TitrationEntryEntity::class,
        InjectionRecordEntity::class,
        AppointmentEntity::class,
        NauseaEntryEntity::class,
        InjectionSiteEntryEntity::class,
        CravingEntryEntity::class,
        SleepEntryEntity::class,
        MoodEntryEntity::class,
        EnergyEntryEntity::class,
        GutEntryEntity::class,
        InflammationEntryEntity::class,
        SensitivityEntryEntity::class,
        BloodSugarEntryEntity::class,
        BpEntryEntity::class,
        StressEntryEntity::class,
        ExerciseEntryEntity::class,
        MeasurementEntryEntity::class,
        CholesterolEntryEntity::class,
        HeartRateEntryEntity::class,
        A1CEntryEntity::class,
        StepEntryEntity::class,
        BodyFatEntryEntity::class,
        FiberEntryEntity::class,
        AlcoholEntryEntity::class,
        CaffeineEntryEntity::class,
        SugarEntryEntity::class,
        SodiumEntryEntity::class,
        MacroEntryEntity::class,
        ProteinEntryEntity::class,
        CalorieEntryEntity::class,
        NutrientEntryEntity::class,
        HungerEntryEntity::class,
        MealTimingEntryEntity::class,
        MindfulMealEntryEntity::class,
        MilestoneEntryEntity::class,
        NsvEntryEntity::class,
        SideEffectEntryEntity::class,
        LabEntryEntity::class,
        JournalEntryEntity::class,
        CheckInEntryEntity::class,
        Glp1EntryEntity::class,
    ],
    version = 5,
    exportSchema = false,
)
abstract class NutraHelpDatabase : RoomDatabase() {
    abstract fun weightDao(): WeightDao
    abstract fun waterDao(): WaterDao
    abstract fun diaryDao(): DiaryDao
    abstract fun titrationDao(): TitrationDao
    abstract fun injectionDao(): InjectionDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun nauseaDao(): NauseaDao
    abstract fun injectionSiteDao(): InjectionSiteDao
    abstract fun cravingDao(): CravingDao
    abstract fun sleepDao(): SleepDao
    abstract fun moodDao(): MoodDao
    abstract fun energyDao(): EnergyDao
    abstract fun gutDao(): GutDao
    abstract fun inflammationDao(): InflammationDao
    abstract fun sensitivityDao(): SensitivityDao
    abstract fun bloodSugarDao(): BloodSugarDao
    abstract fun bpDao(): BpDao
    abstract fun stressDao(): StressDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun cholesterolDao(): CholesterolDao
    abstract fun heartRateDao(): HeartRateDao
    abstract fun a1cDao(): A1CDao
    abstract fun stepDao(): StepDao
    abstract fun bodyFatDao(): BodyFatDao
    abstract fun fiberDao(): FiberDao
    abstract fun alcoholDao(): AlcoholDao
    abstract fun caffeineDao(): CaffeineDao
    abstract fun sugarDao(): SugarDao
    abstract fun sodiumDao(): SodiumDao
    abstract fun macroDao(): MacroDao
    abstract fun proteinDao(): ProteinDao
    abstract fun calorieDao(): CalorieDao
    abstract fun nutrientDao(): NutrientDao
    abstract fun hungerDao(): HungerDao
    abstract fun mealTimingDao(): MealTimingDao
    abstract fun mindfulMealDao(): MindfulMealDao
    abstract fun milestoneDao(): MilestoneDao
    abstract fun nsvDao(): NsvDao
    abstract fun sideEffectDao(): SideEffectDao
    abstract fun labDao(): LabDao
    abstract fun journalDao(): JournalDao
    abstract fun checkInDao(): CheckInDao
    abstract fun glp1Dao(): Glp1Dao

    companion object {
        @Volatile private var INSTANCE: NutraHelpDatabase? = null

        fun getInstance(context: Context): NutraHelpDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NutraHelpDatabase::class.java,
                    "nutrahelp.db"
                ).fallbackToDestructiveMigration(true).build().also { INSTANCE = it }
            }
    }
}