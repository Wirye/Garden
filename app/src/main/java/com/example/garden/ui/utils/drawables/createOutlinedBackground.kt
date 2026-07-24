package com.example.garden.ui.utils.drawables

import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.toColorInt
import com.example.garden.database.SizeType
import com.example.garden.ui.utils.getAdaptiveRadius
import com.example.garden.ui.utils.mathExtensions.applyAlpha

fun createOutlinedbackground(cornerRadiuss: SizeType, width: Int, strokeWidth: Int, strokeAlpha: Float = 0.5f): GradientDrawable {
    return GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor("#BF1B1B1B".toColorInt())
        setStroke(strokeWidth, "#9C9C9C".toColorInt().applyAlpha(strokeAlpha))
        cornerRadius = getAdaptiveRadius(width, cornerRadiuss)
    }
}