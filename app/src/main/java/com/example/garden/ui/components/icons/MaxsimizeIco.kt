package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MaxsimizeIco: ImageVector
    get() {
        if (_MaxsimizeIco != null) {
            return _MaxsimizeIco!!
        }
        _MaxsimizeIco = ImageVector.Builder(
            name = "MaxsimizeIco",
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
                moveTo(19.5f, 6.5f)
                horizontalLineTo(6.5f)
                lineTo(6.5f, 19.5f)
                moveTo(6.5f, 6.5f)
                lineTo(21.667f, 21.667f)
                moveTo(32.5f, 45.5f)
                horizontalLineTo(45.5f)
                verticalLineTo(32.5f)
                moveTo(45.5f, 45.5f)
                lineTo(30.333f, 30.333f)
            }
        }.build()

        return _MaxsimizeIco!!
    }

@Suppress("ObjectPropertyName")
private var _MaxsimizeIco: ImageVector? = null
