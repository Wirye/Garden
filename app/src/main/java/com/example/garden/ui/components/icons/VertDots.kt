package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val VertDots: ImageVector
    get() {
        if (_VertDots != null) {
            return _VertDots!!
        }
        _VertDots = ImageVector.Builder(
            name = "VertDots",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color(0xFFD9D9D9))) {
                moveTo(25.5f, 11.5f)
                moveToRelative(-4.5f, 0f)
                arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 9f, 0f)
                arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -9f, 0f)
            }
            path(fill = SolidColor(Color(0xFFD9D9D9))) {
                moveTo(25.5f, 40.3f)
                moveToRelative(-4.5f, 0f)
                arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 9f, 0f)
                arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -9f, 0f)
            }
            path(fill = SolidColor(Color(0xFFD9D9D9))) {
                moveTo(25.5f, 25.9f)
                moveToRelative(-4.5f, 0f)
                arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 9f, 0f)
                arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -9f, 0f)
            }
        }.build()

        return _VertDots!!
    }

@Suppress("ObjectPropertyName")
private var _VertDots: ImageVector? = null
