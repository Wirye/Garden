package com.example.garden.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.PopupProperties
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.theme.windowInfo
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun DropDownMenuWithBlur(
    modifier: Modifier = Modifier,
    expanded: () -> Boolean,
    onDismissRequest: () -> Unit,
    hazeState: HazeState,
    content: @Composable () -> Unit
) {
    val shape = MaterialTheme.shapes.extraLarge
    val strokeWidth = MaterialTheme.dimens.strokeThick
    DropdownMenu(
        expanded = expanded(),
        onDismissRequest = onDismissRequest,
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier.clip(shape)
            .hazeEffect(
                style = HazeMaterials.regular(),
                state = hazeState
            )
            .border(
                width = strokeWidth,
                color = MaterialTheme.colorScheme.secondary,
                shape = shape
            ),
        properties = PopupProperties(
            focusable = true,
            clippingEnabled = false
        )
    ) {
        Box(
            modifier = Modifier.padding(horizontal = strokeWidth + MaterialTheme.spacing.small, vertical = MaterialTheme.spacing.extraSmall)
        ) {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .widthIn(max = min(MaterialTheme.dimens.maxPopupElementWidth, MaterialTheme.windowInfo.widthDp))
            ) {
                content()
            }
        }
    }
}
