package com.example.nutrahelp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_entries ORDER BY id DESC")
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
    @Query("SELECT * FROM diary_entries WHERE date = :date ORDER BY id ASC")
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