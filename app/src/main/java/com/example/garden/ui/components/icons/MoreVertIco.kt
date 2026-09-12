package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MoreVertIco: ImageVector
    get() {
        if (_MoreVertIco != null) {
            return _MoreVertIco!!
        }
        _MoreVertIco = ImageVector.Builder(
            name = "MoreVertIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color(0xFFFFFEFE))) {
                moveTo(26f, 43.333f)
                curveTo(24.808f, 43.333f, 23.788f, 42.909f, 22.94f, 42.06f)
                curveTo(22.091f, 41.212f, 21.667f, 40.192f, 21.667f, 39f)
                curveTo(21.667f, 37.808f, 22.091f, 36.788f, 22.94f, 35.94f)
                curveTo(23.788f, 35.091f, 24.808f, 34.667f, 26f, 34.667f)
                curveTo(27.192f, 34.667f, 28.212f, 35.091f, 29.06f, 35.94f)
                curveTo(29.909f, 36.788f, 30.333f, 37.808f, 30.333f, 39f)
                curveTo(30.333f, 40.192f, 29.909f, 41.212f, 29.06f, 42.06f)
                curveTo(28.212f, 42.909f, 27.192f, 43.333f, 26f, 43.333f)
                close()
                moveTo(26f, 30.333f)
                curveTo(24.808f, 30.333f, 23.788f, 29.909f, 22.94f, 29.06f)
                curveTo(22.091f, 28.212f, 21.667f, 27.192f, 21.667f, 26f)
                curveTo(21.667f, 24.808f, 22.091f, 23.788f, 22.94f, 22.94f)
                curveTo(23.788f, 22.091f, 24.808f, 21.667f, 26f, 21.667f)
                curveTo(27.192f, 21.667f, 28.212f, 22.091f, 29.06f, 22.94f)
                curveTo(29.909f, 23.788f, 30.333f, 24.808f, 30.333f, 26f)
                curveTo(30.333f, 27.192f, 29.909f, 28.212f, 29.06f, 29.06f)
                curveTo(28.212f, 29.909f, 27.192f, 30.333f, 26f, 30.333f)
                close()
                moveTo(26f, 17.333f)
                curveTo(24.808f, 17.333f, 23.788f, 16.909f, 22.94f, 16.06f)
                curveTo(22.091f, 15.212f, 21.667f, 14.192f, 21.667f, 13f)
                curveTo(21.667f, 11.808f, 22.091f, 10.788f, 22.94f, 9.94f)
                curveTo(23.788f, 9.091f, 24.808f, 8.667f, 26f, 8.667f)
                curveTo(27.192f, 8.667f, 28.212f, 9.091f, 29.06f, 9.94f)
                curveTo(29.909f, 10.788f, 30.333f, 11.808f, 30.333f, 13f)
                curveTo(30.333f, 14.192f, 29.909f, 15.212f, 29.06f, 16.06f)
                curveTo(28.212f, 16.909f, 27.192f, 17.333f, 26f, 17.333f)
                close()
            }
        }.build()

        return _MoreVertIco!!
    }

@Suppress("ObjectPropertyName")
private var _MoreVertIco: ImageVector? = null
