package com.example.garden.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.garden.database.SizeType

@Composable
fun SizeType.toShape() = when (this) {
    SizeType.ESMALL -> MaterialTheme.shapes.extraSmall
    SizeType.SMALL -> MaterialTheme.shapes.small
    SizeType.MEDIUM -> MaterialTheme.shapes.medium
    SizeType.LARGE -> MaterialTheme.shapes.large
    SizeType.XLARGE -> MaterialTheme.shapes.extraLarge
}