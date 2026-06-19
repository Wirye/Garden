package com.example.garden.ui.utils.drawables

import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import com.example.garden.delitRad
import com.example.garden.ui.utils.colors.getDeepDarkColor

fun blobInit(size: Int, color: String, positions: FloatArray = floatArrayOf(0f, 1f), alphaRatio: Float = 0.3f): ShapeDrawable {
    val res = ShapeDrawable(OvalShape()).apply {
        val color1 = color.toColorInt()
        val colors = intArrayOf(ColorUtils.setAlphaComponent(color1, (255 * alphaRatio).toInt()), ColorUtils.setAlphaComponent(getDeepDarkColor(color1), (255 * 0.0).toInt()))

        shaderFactory = object : ShapeDrawable.ShaderFactory() {
            override fun resize(p0: Int, p1: Int): Shader {
                return RadialGradient(
                    size / 2f, size / 2f, // Центр
                    size / delitRad,             // Радиус
                    colors,
                    positions,
                    Shader.TileMode.CLAMP
                )
            }
        }
    }
    return res
}