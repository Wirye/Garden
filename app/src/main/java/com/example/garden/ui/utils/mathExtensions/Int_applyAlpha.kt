package com.example.garden.ui.utils.mathExtensions

fun Int.applyAlpha(alpha: Float): Int {
    val alphaInt = (alpha.coerceIn(0f, 1f) * 255).toInt()
    return (this and 0x00FFFFFF) or (alphaInt shl 24)
}