package com.example.garden.ui.utils

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.example.garden.ui.utils.system.hideKeyboardd

fun dispatchTouchEventHideKeyboard(ev: MotionEvent?, currentFocus: View?) {
    if (ev?.action == MotionEvent.ACTION_DOWN) {
        val v = currentFocus
        if (v is EditText) {
            val outRect = Rect()
            v.getGlobalVisibleRect(outRect)
            if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                v.clearFocus()
                hideKeyboardd(v)
            }
        }
    }
}