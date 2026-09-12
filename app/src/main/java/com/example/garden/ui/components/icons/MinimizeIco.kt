package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MinimizeIco: ImageVector
    get() {
        if (_MinimizeIco != null) {
            return _MinimizeIco!!
        }
        _MinimizeIco = ImageVector.Builder(
            name = "MinimizeIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(21.667f, 43.333f)
                verticalLineTo(30.333f)
                horizontalLineTo(8.667f)
                moveTo(21.667f, 30.333f)
                lineTo(6.5f, 45.5f)
                moveTo(30.333f, 8.667f)
                verticalLineTo(21.667f)
                horizontalLineTo(43.333f)
                moveTo(30.333f, 21.667f)
                lineTo(45.5f, 6.5f)
            }
        }.build()

        return _MinimizeIco!!
    }

@Suppress("ObjectPropertyName")
private var _MinimizeIco: ImageVector? = null
