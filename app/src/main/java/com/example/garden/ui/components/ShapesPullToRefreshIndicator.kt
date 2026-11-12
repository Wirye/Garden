package com.example.garden.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first

private val PullIndicatorElevation = 4.dp

/**
 * Индикатор для Modifier.pullToRefresh.
 * Пока тянешь: контейнер выезжает сверху, фигура крутится по степени растяжения.
 * После отпускания (isRefreshing = true): крутится сама.
 * Кладётся в Box поверх списка, например с Modifier.align(Alignment.TopCenter).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShapesPullToRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    threshold: Dp = PullToRefreshDefaults.PositionalThreshold
) {
    // После окончания загрузки продолжаем показывать крутящийся индикатор,
    // пока он не уедет наверх, иначе фигура дёрнется при смене режима.
    var isSpinning by remember { mutableStateOf(isRefreshing) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            isSpinning = true
        } else {
            snapshotFlow { state.distanceFraction }.first { it <= 0.01f }
            isSpinning = false
        }
    }

    Box(
        modifier = modifier.graphicsLayer {
            val fraction = state.distanceFraction
            val visible = fraction.coerceIn(0f, 1f)
            // за порогом тянется с сопротивлением
            val pulled = if (fraction > 1f) 1f + (fraction - 1f) * 0.4f else fraction

            translationY = pulled * threshold.toPx() - size.height
            scaleX = visible
            scaleY = visible
            alpha = visible
        }
    ) {
        if (isRefreshing || isSpinning) {
            ContainedShapesLoadingIndicator(elevation = PullIndicatorElevation)
        } else {
            ContainedShapesLoadingIndicator(
                progress = { state.distanceFraction },
                elevation = PullIndicatorElevation
            )
        }
    }
}
