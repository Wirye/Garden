package com.example.garden.ui.utils.colors

import androidx.core.graphics.ColorUtils

fun getDeepDarkColor(color: Int): Int {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color, hsl)
    hsl[1] = 0.85f
    hsl[2] = 0.08f
    return ColorUtils.HSLToColor(hsl)
}