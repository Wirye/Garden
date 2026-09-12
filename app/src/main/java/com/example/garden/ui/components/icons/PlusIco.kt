package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PlusIco: ImageVector
    get() {
        if (_PlusIco != null) {
            return _PlusIco!!
        }
        _PlusIco = ImageVector.Builder(
            name = "PlusIco",
            defaultWidth = 96.dp,
            defaultHeight = 96.dp,
            viewportWidth = 96f,
            viewportHeight = 96f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFFB0B0B0)),
                strokeLineWidth = 15f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(47f, 12f)
                verticalLineTo(48f)
                moveTo(47f, 84f)
                verticalLineTo(48f)
                moveTo(47f, 48f)
                horizontalLineTo(83f)
                moveTo(83f, 48f)
                horizontalLineTo(12f)
                moveTo(83f, 48f)
                horizontalLineTo(84f)
            }
        }.build()

        return _PlusIco!!
    }

@Suppress("ObjectPropertyName")
private var _PlusIco: ImageVector? = null
