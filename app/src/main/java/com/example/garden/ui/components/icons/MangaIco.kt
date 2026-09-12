package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MangaIco: ImageVector
    get() {
        if (_MangaIco != null) {
            return _MangaIco!!
        }
        _MangaIco = ImageVector.Builder(
            name = "MangaIco",
            defaultWidth = 96.dp,
            defaultHeight = 96.dp,
            viewportWidth = 96f,
            viewportHeight = 96f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 6f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(85f, 89f)
                horizontalLineTo(78.669f)
                moveTo(10f, 74.5f)
                verticalLineTo(15f)
                curveTo(10f, 10.029f, 14.029f, 6f, 19f, 6f)
                horizontalLineTo(76f)
                curveTo(80.971f, 6f, 85f, 10.029f, 85f, 15f)
                verticalLineTo(73.5f)
                curveTo(85f, 74.052f, 84.552f, 74.5f, 84f, 74.5f)
                horizontalLineTo(78.669f)
                moveTo(10f, 74.5f)
                horizontalLineTo(16.818f)
                moveTo(10f, 74.5f)
                verticalLineTo(81.5f)
                moveTo(78.669f, 74.5f)
                verticalLineTo(89f)
                moveTo(78.669f, 74.5f)
                horizontalLineTo(16.818f)
                moveTo(78.669f, 89f)
                horizontalLineTo(17.5f)
                curveTo(13.358f, 89f, 10f, 85.642f, 10f, 81.5f)
                moveTo(16.818f, 74.5f)
                curveTo(11.364f, 74.5f, 10f, 79.5f, 10f, 81.5f)
            }
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 8f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(29f, 26f)
                horizontalLineTo(63.5f)
            }
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 8f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(29f, 42f)
                horizontalLineTo(63.5f)
            }
        }.build()

        return _MangaIco!!
    }

@Suppress("ObjectPropertyName")
private var _MangaIco: ImageVector? = null
