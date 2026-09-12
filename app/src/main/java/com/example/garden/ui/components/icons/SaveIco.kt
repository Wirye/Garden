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

val SaveIco: ImageVector
    get() {
        if (_SaveIco != null) {
            return _SaveIco!!
        }
        _SaveIco = ImageVector.Builder(
            name = "SaveIco",
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
                    moveTo(45.333f, 56f)
                    verticalLineTo(34.667f)
                    horizontalLineTo(18.667f)
                    verticalLineTo(56f)
                    moveTo(18.667f, 8f)
                    verticalLineTo(21.333f)
                    horizontalLineTo(40f)
                    moveTo(50.667f, 56f)
                    horizontalLineTo(13.333f)
                    curveTo(11.919f, 56f, 10.562f, 55.438f, 9.562f, 54.438f)
                    curveTo(8.562f, 53.438f, 8f, 52.081f, 8f, 50.667f)
                    verticalLineTo(13.333f)
                    curveTo(8f, 11.919f, 8.562f, 10.562f, 9.562f, 9.562f)
                    curveTo(10.562f, 8.562f, 11.919f, 8f, 13.333f, 8f)
                    horizontalLineTo(42.667f)
                    lineTo(56f, 21.333f)
                    verticalLineTo(50.667f)
                    curveTo(56f, 52.081f, 55.438f, 53.438f, 54.438f, 54.438f)
                    curveTo(53.438f, 55.438f, 52.081f, 56f, 50.667f, 56f)
                    close()
                }
            }
        }.build()

        return _SaveIco!!
    }

@Suppress("ObjectPropertyName")
private var _SaveIco: ImageVector? = null
