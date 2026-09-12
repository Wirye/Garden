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

val LogoutIco: ImageVector
    get() {
        if (_LogoutIco != null) {
            return _LogoutIco!!
        }
        _LogoutIco = ImageVector.Builder(
            name = "LogoutIco",
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
                    moveTo(24f, 56f)
                    horizontalLineTo(13.333f)
                    curveTo(11.919f, 56f, 10.562f, 55.438f, 9.562f, 54.438f)
                    curveTo(8.562f, 53.438f, 8f, 52.081f, 8f, 50.667f)
                    verticalLineTo(13.333f)
                    curveTo(8f, 11.919f, 8.562f, 10.562f, 9.562f, 9.562f)
                    curveTo(10.562f, 8.562f, 11.919f, 8f, 13.333f, 8f)
                    horizontalLineTo(24f)
                    moveTo(42.667f, 18.667f)
                    lineTo(56f, 32f)
                    lineTo(42.667f, 45.333f)
                    moveTo(56f, 32f)
                    horizontalLineTo(24f)
                }
            }
        }.build()

        return _LogoutIco!!
    }

@Suppress("ObjectPropertyName")
private var _LogoutIco: ImageVector? = null
