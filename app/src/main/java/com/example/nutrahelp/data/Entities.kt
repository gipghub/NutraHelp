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