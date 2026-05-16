package com.example.nutrahelp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_entries ORDER BY date DESC, id DESC")
    fun getAll(): Flow<List<WeightEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WeightEntryEntity)

    @Delete
    suspend fun delete(entry: WeightEntryEntity)

    @Query("DELETE FROM weight_entries")
    suspend fun deleteAll()
}

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_entries WHERE date = :date ORDER BY id DESC")
    fun getForDate(date: String): Flow<List<WaterEntryEntity>>

    @Query("SELECT date, SUM(amountMl) as total FROM water_entries GROUP BY date ORDER BY date DESC LIMIT 7")
    fun getWeeklyTotals(): Flow<List<DateTotal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WaterEntryEntity)

    @Delete
    suspend fun delete(entry: WaterEntryEntity)

    @Query("DELETE FROM water_entries WHERE date = :date")
    suspend fun deleteForDate(date: String)
}

data class DateTotal(val date: String, val total: Int)

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entries WHERE date = :date ORDER BY id DESC")
    fun getForDate(date: String): Flow<List<DiaryEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DiaryEntryEntity)

    @Delete
    suspend fun delete(entry: DiaryEntryEntity)
}

@Dao
interface TitrationDao {
    @Query("SELECT * FROM titration_entries ORDER BY id DESC")
    fun getAll(): Flow<List<TitrationEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: TitrationEntryEntity)

    @Delete
    suspend fun delete(entry: TitrationEntryEntity)
}

@Dao
interface InjectionDao {
    @Query("SELECT * FROM injection_records ORDER BY id DESC")
    fun getAll(): Flow<List<InjectionRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: InjectionRecordEntity)

    @Delete
    suspend fun delete(entry: InjectionRecordEntity)
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY id DESC")
    fun getAll(): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: AppointmentEntity)

    @Update
    suspend fun update(entry: AppointmentEntity)

    @Delete
    suspend fun delete(entry: AppointmentEntity)
}

@Dao
interface NauseaDao {
    @Query("SELECT * FROM nausea_entries ORDER BY id DESC")
    fun getAll(): Flow<List<NauseaEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: NauseaEntryEntity)

    @Delete
    suspend fun delete(entry: NauseaEntryEntity)

    @Query("DELETE FROM nausea_entries")
    suspend fun deleteAll()
}

@Dao
interface InjectionSiteDao {
    @Query("SELECT * FROM injection_site_entries ORDER BY timestampMs DESC")
    fun getAll(): Flow<List<InjectionSiteEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: InjectionSiteEntryEntity)

    @Delete
    suspend fun delete(entry: InjectionSiteEntryEntity)

    @Query("DELETE FROM injection_site_entries")
    suspend fun deleteAll()
}

@Dao
interface CravingDao {
    @Query("SELECT * FROM craving_entries ORDER BY id DESC")
    fun getAll(): Flow<List<CravingEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CravingEntryEntity)

    @Delete
    suspend fun delete(entry: CravingEntryEntity)

    @Query("DELETE FROM craving_entries")
    suspend fun deleteAll()
}

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_entries ORDER BY id DESC")
    fun getAll(): Flow<List<SleepEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SleepEntryEntity)

    @Delete
    suspend fun delete(entry: SleepEntryEntity)

    @Query("DELETE FROM sleep_entries")
    suspend fun deleteAll()
}

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_entries ORDER BY id DESC")
    fun getAll(): Flow<List<MoodEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MoodEntryEntity)

    @Delete
    suspend fun delete(entry: MoodEntryEntity)

    @Query("DELETE FROM mood_entries")
    suspend fun deleteAll()
}

@Dao
interface EnergyDao {
    @Query("SELECT * FROM energy_entries ORDER BY id DESC")
    fun getAll(): Flow<List<EnergyEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: EnergyEntryEntity)

    @Delete
    suspend fun delete(entry: EnergyEntryEntity)

    @Query("DELETE FROM energy_entries")
    suspend fun deleteAll()
}

@Dao
interface GutDao {
    @Query("SELECT * FROM gut_entries ORDER BY id DESC")
    fun getAll(): Flow<List<GutEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: GutEntryEntity)

    @Delete
    suspend fun delete(entry: GutEntryEntity)

    @Query("DELETE FROM gut_entries")
    suspend fun deleteAll()
}

@Dao
interface InflammationDao {
    @Query("SELECT * FROM inflammation_entries ORDER BY id DESC")
    fun getAll(): Flow<List<InflammationEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: InflammationEntryEntity)

    @Delete
    suspend fun delete(entry: InflammationEntryEntity)

    @Query("DELETE FROM inflammation_entries")
    suspend fun deleteAll()
}

@Dao
interface SensitivityDao {
    @Query("SELECT * FROM sensitivity_entries ORDER BY id DESC")
    fun getAll(): Flow<List<SensitivityEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SensitivityEntryEntity)

    @Delete
    suspend fun delete(entry: SensitivityEntryEntity)

    @Query("DELETE FROM sensitivity_entries")
    suspend fun deleteAll()
}

@Dao
interface BloodSugarDao {
    @Query("SELECT * FROM blood_sugar_entries ORDER BY id DESC")
    fun getAll(): Flow<List<BloodSugarEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: BloodSugarEntryEntity)

    @Delete
    suspend fun delete(entry: BloodSugarEntryEntity)

    @Query("DELETE FROM blood_sugar_entries")
    suspend fun deleteAll()
}

@Dao
interface BpDao {
    @Query("SELECT * FROM bp_entries ORDER BY id DESC")
    fun getAll(): Flow<List<BpEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: BpEntryEntity)

    @Delete
    suspend fun delete(entry: BpEntryEntity)

    @Query("DELETE FROM bp_entries")
    suspend fun deleteAll()
}

@Dao
interface StressDao {
    @Query("SELECT * FROM stress_entries ORDER BY id DESC")
    fun getAll(): Flow<List<StressEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: StressEntryEntity)

    @Delete
    suspend fun delete(entry: StressEntryEntity)

    @Query("DELETE FROM stress_entries")
    suspend fun deleteAll()
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise_entries ORDER BY id DESC")
    fun getAll(): Flow<List<ExerciseEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ExerciseEntryEntity)

    @Delete
    suspend fun delete(entry: ExerciseEntryEntity)

    @Query("DELETE FROM exercise_entries")
    suspend fun deleteAll()
}

@Dao
interface MeasurementDao {
    @Query("SELECT * FROM measurement_entries ORDER BY id DESC")
    fun getAll(): Flow<List<MeasurementEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MeasurementEntryEntity)

    @Delete
    suspend fun delete(entry: MeasurementEntryEntity)

    @Query("DELETE FROM measurement_entries")
    suspend fun deleteAll()
}

@Dao
interface CholesterolDao {
    @Query("SELECT * FROM cholesterol_entries ORDER BY id DESC")
    fun getAll(): Flow<List<CholesterolEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CholesterolEntryEntity)

    @Delete
    suspend fun delete(entry: CholesterolEntryEntity)

    @Query("DELETE FROM cholesterol_entries")
    suspend fun deleteAll()
}

@Dao
interface HeartRateDao {
    @Query("SELECT * FROM heart_rate_entries ORDER BY id DESC")
    fun getAll(): Flow<List<HeartRateEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HeartRateEntryEntity)

    @Delete
    suspend fun delete(entry: HeartRateEntryEntity)

    @Query("DELETE FROM heart_rate_entries")
    suspend fun deleteAll()
}

@Dao
interface A1CDao {
    @Query("SELECT * FROM a1c_entries ORDER BY id DESC")
    fun getAll(): Flow<List<A1CEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: A1CEntryEntity)

    @Delete
    suspend fun delete(entry: A1CEntryEntity)

    @Query("DELETE FROM a1c_entries")
    suspend fun deleteAll()
}

@Dao
interface StepDao {
    @Query("SELECT * FROM step_entries ORDER BY id DESC")
    fun getAll(): Flow<List<StepEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: StepEntryEntity)

    @Delete
    suspend fun delete(entry: StepEntryEntity)

    @Query("DELETE FROM step_entries")
    suspend fun deleteAll()
}

@Dao
interface BodyFatDao {
    @Query("SELECT * FROM body_fat_entries ORDER BY id DESC")
    fun getAll(): Flow<List<BodyFatEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: BodyFatEntryEntity)

    @Delete
    suspend fun delete(entry: BodyFatEntryEntity)

    @Query("DELETE FROM body_fat_entries")
    suspend fun deleteAll()
}

@Dao
interface FiberDao {
    @Query("SELECT * FROM fiber_entries ORDER BY id DESC")
    fun getAll(): Flow<List<FiberEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FiberEntryEntity)

    @Delete
    suspend fun delete(entry: FiberEntryEntity)

    @Query("DELETE FROM fiber_entries")
    suspend fun deleteAll()
}

@Dao
interface AlcoholDao {
    @Query("SELECT * FROM alcohol_entries ORDER BY id DESC")
    fun getAll(): Flow<List<AlcoholEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: AlcoholEntryEntity)

    @Delete
    suspend fun delete(entry: AlcoholEntryEntity)

    @Query("DELETE FROM alcohol_entries")
    suspend fun deleteAll()
}

@Dao
interface CaffeineDao {
    @Query("SELECT * FROM caffeine_entries ORDER BY id DESC")
    fun getAll(): Flow<List<CaffeineEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CaffeineEntryEntity)

    @Delete
    suspend fun delete(entry: CaffeineEntryEntity)

    @Query("DELETE FROM caffeine_entries")
    suspend fun deleteAll()
}

@Dao
interface SugarDao {
    @Query("SELECT * FROM sugar_entries ORDER BY id DESC")
    fun getAll(): Flow<List<SugarEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SugarEntryEntity)

    @Delete
    suspend fun delete(entry: SugarEntryEntity)

    @Query("DELETE FROM sugar_entries")
    suspend fun deleteAll()
}

@Dao
interface SodiumDao {
    @Query("SELECT * FROM sodium_entries ORDER BY id DESC")
    fun getAll(): Flow<List<SodiumEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SodiumEntryEntity)

    @Delete
    suspend fun delete(entry: SodiumEntryEntity)

    @Query("DELETE FROM sodium_entries")
    suspend fun deleteAll()
}

@Dao
interface MacroDao {
    @Query("SELECT * FROM macro_entries ORDER BY id DESC")
    fun getAll(): Flow<List<MacroEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MacroEntryEntity)

    @Delete
    suspend fun delete(entry: MacroEntryEntity)

    @Query("DELETE FROM macro_entries")
    suspend fun deleteAll()
}

@Dao
interface ProteinDao {
    @Query("SELECT * FROM protein_entries ORDER BY id DESC")
    fun getAll(): Flow<List<ProteinEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ProteinEntryEntity)

    @Delete
    suspend fun delete(entry: ProteinEntryEntity)

    @Query("DELETE FROM protein_entries")
    suspend fun deleteAll()
}

@Dao
interface CalorieDao {
    @Query("SELECT * FROM calorie_entries ORDER BY id DESC")
    fun getAll(): Flow<List<CalorieEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CalorieEntryEntity)

    @Delete
    suspend fun delete(entry: CalorieEntryEntity)

    @Query("DELETE FROM calorie_entries")
    suspend fun deleteAll()
}

@Dao
interface NutrientDao {
    @Query("SELECT * FROM nutrient_entries ORDER BY id DESC")
    fun getAll(): Flow<List<NutrientEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: NutrientEntryEntity)

    @Delete
    suspend fun delete(entry: NutrientEntryEntity)

    @Query("DELETE FROM nutrient_entries")
    suspend fun deleteAll()
}

@Dao
interface HungerDao {
    @Query("SELECT * FROM hunger_entries ORDER BY id DESC")
    fun getAll(): Flow<List<HungerEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HungerEntryEntity)

    @Delete
    suspend fun delete(entry: HungerEntryEntity)

    @Query("DELETE FROM hunger_entries")
    suspend fun deleteAll()
}

@Dao
interface MealTimingDao {
    @Query("SELECT * FROM meal_timing_entries ORDER BY minutesSinceMidnight ASC")
    fun getAll(): Flow<List<MealTimingEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MealTimingEntryEntity)

    @Delete
    suspend fun delete(entry: MealTimingEntryEntity)

    @Query("DELETE FROM meal_timing_entries")
    suspend fun deleteAll()
}

@Dao
interface MindfulMealDao {
    @Query("SELECT * FROM mindful_meal_entries ORDER BY id DESC")
    fun getAll(): Flow<List<MindfulMealEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MindfulMealEntryEntity)

    @Delete
    suspend fun delete(entry: MindfulMealEntryEntity)

    @Query("DELETE FROM mindful_meal_entries")
    suspend fun deleteAll()
}

@Dao
interface MilestoneDao {
    @Query("SELECT * FROM milestone_entries ORDER BY id DESC")
    fun getAll(): Flow<List<MilestoneEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MilestoneEntryEntity)

    @Delete
    suspend fun delete(entry: MilestoneEntryEntity)

    @Query("DELETE FROM milestone_entries")
    suspend fun deleteAll()
}

@Dao
interface NsvDao {
    @Query("SELECT * FROM nsv_entries ORDER BY id DESC")
    fun getAll(): Flow<List<NsvEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: NsvEntryEntity)

    @Delete
    suspend fun delete(entry: NsvEntryEntity)

    @Query("DELETE FROM nsv_entries")
    suspend fun deleteAll()
}

@Dao
interface SideEffectDao {
    @Query("SELECT * FROM side_effect_entries ORDER BY id DESC")
    fun getAll(): Flow<List<SideEffectEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SideEffectEntryEntity)

    @Delete
    suspend fun delete(entry: SideEffectEntryEntity)

    @Query("DELETE FROM side_effect_entries")
    suspend fun deleteAll()
}

@Dao
interface LabDao {
    @Query("SELECT * FROM lab_entries ORDER BY id DESC")
    fun getAll(): Flow<List<LabEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: LabEntryEntity)

    @Delete
    suspend fun delete(entry: LabEntryEntity)

    @Query("DELETE FROM lab_entries")
    suspend fun deleteAll()
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY id DESC")
    fun getAll(): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntryEntity)

    @Delete
    suspend fun delete(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries")
    suspend fun deleteAll()
}

@Dao
interface CheckInDao {
    @Query("SELECT * FROM checkin_entries ORDER BY id DESC")
    fun getAll(): Flow<List<CheckInEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CheckInEntryEntity)

    @Delete
    suspend fun delete(entry: CheckInEntryEntity)

    @Query("DELETE FROM checkin_entries")
    suspend fun deleteAll()
}

@Dao
interface Glp1Dao {
    @Query("SELECT * FROM glp1_entries ORDER BY id DESC")
    fun getAll(): Flow<List<Glp1EntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Glp1EntryEntity)

    @Delete
    suspend fun delete(entry: Glp1EntryEntity)

    @Query("DELETE FROM glp1_entries")
    suspend fun deleteAll()
}