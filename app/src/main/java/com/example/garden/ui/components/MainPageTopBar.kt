package com.example.garden.ui.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.example.garden.R
import com.example.garden.ui.screens.LocalHazeLayers
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing

@Composable
fun MainPageTopBar(
    modifier: Modifier = Modifier,
    offsetPx: Float,
    isStrokeVisible: Boolean = true,
    active: Boolean = true,
    heightState: (Dp) -> Unit = {},
    onSearch: () -> Unit,
    onSettings: () -> Unit,
    onAddCarousel: () -> Unit,
    onEdit: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val strokeWidth = with(LocalDensity.current) { MaterialTheme.dimens.strokeExtraThin.toPx() }
    val strokeState = remember { MutableTransitionState(isStrokeVisible) }
    LaunchedEffect(isStrokeVisible) {
        strokeState.targetState = isStrokeVisible
    }
    val strokeTransition = rememberTransition(strokeState, "stroke alpha")
    val strokeAlpha by strokeTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 200) },
        label = "stroke alpha"
    ) { state ->
        if (state)
            0.5f
        else
            0f
    }
    val density = LocalDensity.current
    val topInsetPx = WindowInsets.safeDrawing.getTop(density)
    val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)
    val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)

    val topInset = with(density) { topInsetPx.toDp() }
    val rightInset = with(density) { rightInsetPx.toDp() }
    val leftInset = with(density) { leftInsetPx.toDp() }

    Box(
        modifier = modifier.onGloballyPositioned {
            heightState(with(density) { it.size.height.toDp() })
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = topInset, start = leftInset, end = rightInset)
                .graphicsLayer {
                    translationY = if (active) offsetPx else -Float.MAX_VALUE
                    alpha = if (!active) 0f else 1f
                }
                .background(Color.Transparent)
                .drawBehind {
                    val y = size.height - strokeWidth / 2

                    drawLine(
                        color = Color.White.copy(alpha = strokeAlpha),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
        ) {
            val additionPadding = MaterialTheme.spacing.extraLarge
            val screenHorizontal = MaterialTheme.spacing.screenHorizontal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = additionPadding, horizontal = screenHorizontal),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Row {
                    IconButton(
                        enabled = active,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onSearch()
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.search_ico),
                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        enabled = active,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onSettings()
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.settings_ico),
                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }
                    val isExpanded = remember { mutableStateOf(false) }
                    Box {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                isExpanded.value = !isExpanded.value
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.more_vert_ico),
                                modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = null
                            )
                        }

                        DropDownMenuWithBlur(
                            expanded = { isExpanded.value },
                            hazeState = LocalHazeLayers.current.mainScreen,
                            onDismissRequest = { isExpanded.value = false }
                        ) {
                            PopupMenuItem(
                                text = stringResource(R.string.edit),
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    onEdit()
                                    isExpanded.value = false
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.edit_ico),
                                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = null
                                )
                            }

                            PopupMenuItem(
                                text = stringResource(R.string.addCarousel),
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    onAddCarousel()
                                    isExpanded.value = false
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.add_ico),
                                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

class SmartCollapsingTopBarState(
    val topBarHeightPx: Float,
    val minBarOffset: Float,
    private val listState: LazyListState
) {
    var barOffsetPx by mutableFloatStateOf(0f)
        private set

    var firstElementScrollOffset by mutableFloatStateOf(0f)
        private set

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            val deltaPx = consumed.y
            barOffsetPx = (barOffsetPx + deltaPx).coerceIn(-topBarHeightPx - minBarOffset, 0f)

            firstElementScrollOffset =
                if (listState.firstVisibleItemIndex == 0) listState.firstVisibleItemScrollOffset.toFloat() else Int.MAX_VALUE.toFloat()

            return super.onPostScroll(consumed, available, source)
        }
    }

    fun resetBarOffset() {
        barOffsetPx = 0f
    }
}

@Composable
fun rememberSmartCollapsingTopBarState(
    topBarHeight: Dp,
    minBarOffset: Dp,
    listState: LazyListState
): SmartCollapsingTopBarState {
    val density = LocalDensity.current
    val topBarHeightPx = with(density) { topBarHeight.toPx() }
    val minBarOffset = with(density) { minBarOffset.toPx() }

    return remember(topBarHeightPx, listState, minBarOffset) {
        SmartCollapsingTopBarState(topBarHeightPx, minBarOffset, listState)
    }
}