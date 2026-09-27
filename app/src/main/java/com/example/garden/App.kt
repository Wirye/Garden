package com.example.garden

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class App: Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}