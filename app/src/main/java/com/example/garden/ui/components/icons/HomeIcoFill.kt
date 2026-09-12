package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val HomeIcoFill: ImageVector
    get() {
        if (_HomeIcoFill != null) {
            return _HomeIcoFill!!
        }
        _HomeIcoFill = ImageVector.Builder(
            name = "HomeIcoFill",
            defaultWidth = 96.dp,
            defaultHeight = 96.dp,
            viewportWidth = 96f,
            viewportHeight = 96f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                stroke = SolidColor(Color.White),
                strokeLineWidth = 6f
            ) {
                moveTo(45.336f, 8.26f)
                lineTo(6.776f, 49.11f)
                curveTo(4.97f, 51.023f, 6.326f, 54.169f, 8.957f, 54.169f)
                horizontalLineTo(14f)
                curveTo(15.657f, 54.169f, 17f, 55.512f, 17f, 57.169f)
                verticalLineTo(86f)
                curveTo(17f, 87.657f, 18.343f, 89f, 20f, 89f)
                horizontalLineTo(38.03f)
                curveTo(39.675f, 89f, 41.013f, 87.675f, 41.03f, 86.03f)
                lineTo(41.317f, 57.139f)
                curveTo(41.334f, 55.494f, 42.672f, 54.169f, 44.317f, 54.169f)
                horizontalLineTo(53.037f)
                curveTo(54.695f, 54.169f, 56.039f, 55.514f, 56.037f, 57.173f)
                lineTo(56.004f, 85.996f)
                curveTo(56.002f, 87.655f, 57.345f, 89f, 59.004f, 89f)
                horizontalLineTo(77f)
                curveTo(78.657f, 89f, 80f, 87.657f, 80f, 86f)
                verticalLineTo(57.169f)
                curveTo(80f, 55.512f, 81.343f, 54.169f, 83f, 54.169f)
                horizontalLineTo(87.825f)
                curveTo(90.488f, 54.169f, 91.831f, 50.958f, 89.961f, 49.062f)
                lineTo(49.653f, 8.213f)
                curveTo(48.46f, 7.004f, 46.502f, 7.025f, 45.336f, 8.26f)
                close()
            }
        }.build()

        return _HomeIcoFill!!
    }

@Suppress("ObjectPropertyName")
private var _HomeIcoFill: ImageVector? = null
