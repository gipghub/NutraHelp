package com.example.nutrahelp.data

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry

fun buildTestDb(): NutraHelpDatabase =
    Room.inMemoryDatabaseBuilder(
        InstrumentationRegistry.getInstrumentation().targetContext,
        NutraHelpDatabase::class.java,
    ).allowMainThreadQueries().build()