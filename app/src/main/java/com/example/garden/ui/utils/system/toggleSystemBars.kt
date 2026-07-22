package com.example.garden.ui.utils.system

import android.app.Activity
import android.content.Context
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun toggleSystemBars(show: Boolean, context: Context) {
    val window = (context as? Activity)?.window ?: return
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    if (show) {
        controller.show(WindowInsetsCompat.Type.systemBars())
    }
    else {
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
}