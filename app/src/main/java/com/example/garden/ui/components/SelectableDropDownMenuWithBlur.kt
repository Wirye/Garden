package com.example.garden.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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

@SuppressLint("UseOfNonLambdaOffsetOverload")
@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun SelectableDropDownMenuWithBlur(
    modifier: Modifier = Modifier,
    expanded: () -> Boolean,
    onDismissRequest: () -> Unit,
    hazeState: HazeState,
    items: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val shape = MaterialTheme.shapes.extraLarge
    val strokeWidth = MaterialTheme.dimens.strokeThick
    val density = LocalDensity.current

    val itemHeights = remember { mutableStateMapOf<Int, Int>() }

    val targetOffsetY = remember(selectedIndex, itemHeights.toMap()) {
        var y = 0
        for (i in 0 until selectedIndex) {
            y += itemHeights[i] ?: 0
        }
        with(density) { y.toDp() }
    }

    val targetHeight = remember(selectedIndex, itemHeights.toMap()) {
        val px = itemHeights[selectedIndex] ?: 0
        with(density) { px.toDp() }
    }

    val animatedOffsetY by animateDpAsState(
        targetValue = targetOffsetY,
        animationSpec = tween(durationMillis = 200),
        label = "OffsetY"
    )

    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = tween(durationMillis = 200),
        label = "Height"
    )

    DropdownMenu(
        expanded = expanded(),
        onDismissRequest = onDismissRequest,
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier
            .clip(shape)
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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = strokeWidth + MaterialTheme.spacing.small, vertical = MaterialTheme.spacing.extraSmall)
        ) {
            if (animatedHeight > 0.dp) {
                Box(
                    modifier = Modifier
                        .offset(y = animatedOffsetY)
                        .fillMaxWidth()
                        .height(animatedHeight)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                )
            }

            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .widthIn(
                        max = min(
                            MaterialTheme.dimens.maxPopupElementWidth,
                            MaterialTheme.windowInfo.widthDp
                        )
                    )
            ) {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onSizeChanged { size ->
                                itemHeights[index] = size.height
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onSelect(index)
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (index == selectedIndex)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}