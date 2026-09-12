package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LoginIco: ImageVector
    get() {
        if (_LoginIco != null) {
            return _LoginIco!!
        }
        _LoginIco = ImageVector.Builder(
            name = "LoginIco",
            defaultWidth = 64.dp,
            defaultHeight = 64.dp,
            viewportWidth = 64f,
            viewportHeight = 64f
        ).apply {
            group(
                clipPathData = PathData {
                    moveTo(0f, 0f)
                    horizontalLineToRelative(64f)
                    verticalLineToRelative(64f)
                    horizontalLineToRelative(-64f)
                    close()
                }
            ) {
                path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 4f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(40f, 8f)
                    horizontalLineTo(50.667f)
                    curveTo(52.081f, 8f, 53.438f, 8.562f, 54.438f, 9.562f)
                    curveTo(55.438f, 10.562f, 56f, 11.919f, 56f, 13.333f)
                    verticalLineTo(50.667f)
                    curveTo(56f, 52.081f, 55.438f, 53.438f, 54.438f, 54.438f)
                    curveTo(53.438f, 55.438f, 52.081f, 56f, 50.667f, 56f)
                    horizontalLineTo(40f)
                    moveTo(26.667f, 18.667f)
                    lineTo(40f, 32f)
                    lineTo(26.667f, 45.333f)
                    moveTo(40f, 32f)
                    horizontalLineTo(8f)
                }
            }
        }.build()

        return _LoginIco!!
    }

@Suppress("ObjectPropertyName")
private var _LoginIco: ImageVector? = null
