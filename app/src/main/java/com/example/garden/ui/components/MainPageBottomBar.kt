package com.example.garden.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.example.garden.R
import com.example.garden.database.PageType
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing

@Composable
fun MainPageBottomBar(
    active: Boolean = true,
    heightState: (Dp) -> Unit = {},
    onHomePage: () -> Unit,
    onAnimePage: () -> Unit,
    onMusicPage: () -> Unit,
    onMangaPage: () -> Unit,
    onDownloadsPage: () -> Unit,
    pageState: PageType
) {
    val haptic = LocalHapticFeedback.current

    val density = LocalDensity.current
    Box(
        modifier = Modifier.onGloballyPositioned {
            heightState(with(density) { it.size.height.toDp() })
        }
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Box {
            val buttonColor = MaterialTheme.colorScheme.onSurface

            val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)
            val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)
            val bottomInsetPx = WindowInsets.safeDrawing.getBottom(density)

            val bottomInset = with(density) { bottomInsetPx.toDp() }
            val rightInset = with(density) { rightInsetPx.toDp() }
            val leftInset = with(density) { leftInsetPx.toDp() }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
                    .padding(
                        bottom = bottomInset,
                        start = MaterialTheme.spacing.screenHorizontal +
                                leftInset,
                        end = MaterialTheme.spacing.screenHorizontal +
                                rightInset,
                        top = MaterialTheme.spacing.small
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                    modifier = Modifier.clickable(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onHomePage()
                        },
                        enabled = active)
                        .heightIn(min = MaterialTheme.dimens.minButtonHeight)
                ) {
                    Icon(
                        painter = painterResource(if (pageState == PageType.Home) R.drawable.home_ico_fill else R.drawable.home_ico),
                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                        tint = buttonColor,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.home),
                        style = MaterialTheme.typography.labelMedium,
                        color = buttonColor
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                    modifier = Modifier.clickable(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onAnimePage()
                        },
                        enabled = active)
                        .heightIn(min = MaterialTheme.dimens.minButtonHeight)
                ) {
                    Icon(
                        painter = painterResource(if (pageState == PageType.Anime) R.drawable.anime_ico_fill else R.drawable.anime_ico),
                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                        tint = buttonColor,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.anime),
                        style = MaterialTheme.typography.labelMedium,
                        color = buttonColor
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                    modifier = Modifier.clickable(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onMusicPage()
                        },
                        enabled = active)
                        .heightIn(min = MaterialTheme.dimens.minButtonHeight)
                ) {
                    Icon(
                        painter = painterResource(if (pageState == PageType.Music) R.drawable.music_ico_fill else R.drawable.music_ico),
                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                        tint = buttonColor,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.music),
                        style = MaterialTheme.typography.labelMedium,
                        color = buttonColor
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                    modifier = Modifier.clickable(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onMangaPage()
                        },
                        enabled = active)
                        .heightIn(min = MaterialTheme.dimens.minButtonHeight)
                ) {
                    Icon(
                        painter = painterResource(if (pageState == PageType.Manga) R.drawable.manga_ico_fill else R.drawable.manga_ico),
                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                        tint = buttonColor,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.manga),
                        style = MaterialTheme.typography.labelMedium,
                        color = buttonColor
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                    modifier = Modifier.clickable(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onDownloadsPage()
                        },
                        enabled = active)
                        .heightIn(min = MaterialTheme.dimens.minButtonHeight)
                ) {
                    Icon(
                        painter = painterResource(if (pageState == PageType.Download) R.drawable.download_ico_fill else R.drawable.download_ico),
                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                        tint = buttonColor,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.downloads),
                        style = MaterialTheme.typography.labelMedium,
                        color = buttonColor
                    )
                }
            }
        }
    }
}