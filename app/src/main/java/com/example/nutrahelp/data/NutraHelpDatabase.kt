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
    ],
    version = 1,
    exportSchema = false,
)
abstract class NutraHelpDatabase : RoomDatabase() {
    abstract fun weightDao(): WeightDao
    abstract fun waterDao(): WaterDao
    abstract fun diaryDao(): DiaryDao
    abstract fun titrationDao(): TitrationDao
    abstract fun injectionDao(): InjectionDao

    companion object {
        @Volatile private var INSTANCE: NutraHelpDatabase? = null

        fun getInstance(context: Context): NutraHelpDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NutraHelpDatabase::class.java,
                    "nutrahelp.db"
                ).build().also { INSTANCE = it }
            }
    }
}