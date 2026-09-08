package com.example.garden.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun TextUnit.toDp(): Dp {
    val density = LocalDensity.current
    return with(density) { (this@toDp.value / fontScale).dp }
}