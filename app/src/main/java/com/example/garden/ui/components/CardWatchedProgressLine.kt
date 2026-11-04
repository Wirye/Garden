package com.example.garden.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.garden.LocalCustomColors
import com.example.garden.ui.theme.dimens

@Composable
fun CardWatchedProgressLine(
    progress: () -> Float,
    modifier: Modifier = Modifier,
) {
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier
            .height(MaterialTheme.dimens.strokeThick),
        color = LocalCustomColors.current.closeButton,
        trackColor = Color.Transparent,
        drawStopIndicator = {}
    )
}
