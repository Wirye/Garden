package com.example.garden.ui.utils.viewExtensions

import android.content.res.ColorStateList
import com.example.garden.ui.customView.OutlinedTextField

fun OutlinedTextField.changeStrokeColor(color: Int) {
    val states = arrayOf(
        intArrayOf(android.R.attr.state_focused),
        intArrayOf()
    )
    val colors = intArrayOf(color, color)
    this.setBoxStrokeColorStateList(ColorStateList(states, colors))
    this.invalidate()
    this.requestLayout()
}