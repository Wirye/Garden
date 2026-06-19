package com.example.garden.ui.utils.system

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo

fun changeOrientation(context: Context, isItShouldBeUnspecified: Boolean) {
    val activity = context as? Activity ?: return
    if (!isItShouldBeUnspecified) {
        if (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
    else {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}