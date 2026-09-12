package com.example.garden.ui.components

import android.view.WindowManager
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.example.garden.LocalCustomColors
import com.example.garden.R
import com.example.garden.ui.components.icons.DeleteIco
import com.example.garden.ui.components.icons.EditIco
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun SmartFilePicker(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onFileDelete: () -> Unit,
    onFileChange: () -> Unit,
    hazeState: HazeState
) {
    val density = LocalDensity.current
    val topInsetPx = WindowInsets.safeDrawing.getTop(density)
    val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)
    val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)
    val bottomInsetPx = WindowInsets.safeDrawing.getBottom(density)

    val additionalPadding = MaterialTheme.spacing.screenHorizontal

    val topInset = with(density) { topInsetPx.toDp() } + additionalPadding
    val rightInset = with(density) { rightInsetPx.toDp() } + additionalPadding
    val leftInset = with(density) { leftInsetPx.toDp() } + additionalPadding
    val bottomInset = with(density) { bottomInsetPx.toDp() } + additionalPadding

    val transitionState = remember { MutableTransitionState(expanded) }

    LaunchedEffect(expanded) {
        transitionState.targetState = expanded
    }

    val transition = rememberTransition(transitionState, label = "enter/out")

    val dialogAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 250) },
        label = "dialogAlpha"
    ) { state ->
        if (state) 1f else 0f
    }

    val contentAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 250, delayMillis = 100) },
        label = "contentAlpha"
    ) { state ->
        if (state) 1f else 0f
    }

    if (expanded || transitionState.currentState) {
        val isDarkTheme = isSystemInDarkTheme()
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                decorFitsSystemWindows = false
            )
        ) {
            val haptic = LocalHapticFeedback.current

            val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
            SideEffect {
                dialogWindow?.let { window ->
                    window.setWindowAnimations(0)
                    window.setDimAmount(0f)
                    window.setBackgroundBlurRadius(0)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    val controller = WindowCompat.getInsetsController(window, window.decorView)
                    controller.isAppearanceLightStatusBars = !isDarkTheme
                    controller.isAppearanceLightNavigationBars = !isDarkTheme
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
                    .clickable(onClick = onDismiss)
                    .graphicsLayer {
                        this.alpha = dialogAlpha
                    }
                    .hazeEffect(
                        state = hazeState,
                        style = HazeMaterials.thin()
                    )
                    .padding(
                        top = topInset,
                        bottom = bottomInset,
                        start = leftInset,
                        end = rightInset
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .graphicsLayer {
                            this.alpha = contentAlpha
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                        modifier = Modifier.weight(1f, fill = true)
                    ) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onFileDelete()
                                onDismiss()
                            },
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LocalCustomColors.current.closeButton,
                                contentColor = LocalCustomColors.current.onCloseButton
                            ),
                            contentPadding = PaddingValues(MaterialTheme.spacing.medium)
                        ) {
                            Icon(
                                imageVector = DeleteIco,
                                contentDescription = null,
                                modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                            )
                        }

                        Text(
                            text = stringResource(R.string.delete),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                        modifier = Modifier.weight(1f, fill = true)
                    ) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onFileChange()
                                onDismiss()
                            },
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            contentPadding = PaddingValues(MaterialTheme.spacing.medium)
                        ) {
                            Icon(
                                imageVector = EditIco,
                                contentDescription = null,
                                modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                            )
                        }

                        Text(
                            text = stringResource(R.string.change),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}
