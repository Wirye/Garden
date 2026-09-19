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

val FolderIco: ImageVector
    get() {
        if (_FolderIco != null) {
            return _FolderIco!!
        }
        _FolderIco = ImageVector.Builder(
            name = "FolderIco",
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
                    moveTo(58.667f, 50.667f)
                    curveTo(58.667f, 52.081f, 58.105f, 53.438f, 57.105f, 54.438f)
                    curveTo(56.104f, 55.438f, 54.748f, 56f, 53.333f, 56f)
                    horizontalLineTo(10.667f)
                    curveTo(9.252f, 56f, 7.896f, 55.438f, 6.895f, 54.438f)
                    curveTo(5.895f, 53.438f, 5.333f, 52.081f, 5.333f, 50.667f)
                    verticalLineTo(13.333f)
                    curveTo(5.333f, 11.919f, 5.895f, 10.562f, 6.895f, 9.562f)
                    curveTo(7.896f, 8.562f, 9.252f, 8f, 10.667f, 8f)
                    horizontalLineTo(24f)
                    lineTo(29.333f, 16f)
                    horizontalLineTo(53.333f)
                    curveTo(54.748f, 16f, 56.104f, 16.562f, 57.105f, 17.562f)
                    curveTo(58.105f, 18.562f, 58.667f, 19.919f, 58.667f, 21.333f)
                    verticalLineTo(50.667f)
                    close()
                }
            }
        }.build()

        return _FolderIco!!
    }

@Suppress("ObjectPropertyName")
private var _FolderIco: ImageVector? = null
