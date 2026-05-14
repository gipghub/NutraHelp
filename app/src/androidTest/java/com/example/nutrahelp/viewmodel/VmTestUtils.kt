package com.example.nutrahelp.viewmodel

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry

fun testApp(): Application =
    InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application