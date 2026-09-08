package com.example.garden.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StrokeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    strokeColor: Color = Color.Black,
    strokeWidth: Dp = 1.3.dp,
    style: TextStyle = LocalTextStyle.current,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { strokeWidth.toPx() }

    Box(modifier = modifier) {
        Text(
            text = text,
            color = strokeColor,
            maxLines = maxLines,
            overflow = overflow,
            style = style.copy(
                drawStyle = Stroke(
                    width = strokeWidthPx,
                    join = StrokeJoin.Round,
                    miter = 3f
                )
            )
        )

        Text(
            text = text,
            color = color,
            maxLines = maxLines,
            overflow = overflow,
            style = style
        )
    }
}