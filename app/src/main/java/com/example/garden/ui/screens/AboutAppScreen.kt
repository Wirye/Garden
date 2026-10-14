package com.example.garden.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.garden.R
import com.example.garden.database.entities.ImageData
import com.example.garden.ui.components.AppAsyncImage
import com.example.garden.ui.components.CloseButton
import com.example.garden.ui.components.SettingsBlock
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus

@Composable
fun AboutAppScreen(
    onClose: () -> Unit
) {
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    val uriHandler = LocalUriHandler.current

    val topInsetPx = WindowInsets.safeDrawing.getTop(density)
    val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)
    val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)

    val topInset = with(density) { topInsetPx.toDp() }
    val leftInset = with(density) { leftInsetPx.toDp() }
    val rightInset = with(density) { rightInsetPx.toDp() }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .blockGestures()
            .clearFocus(focusManager)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topInset + MaterialTheme.spacing.screenHorizontal)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(MaterialTheme.dimens.minButtonHeight))

                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = stringResource(R.string.AboutApp),
                    maxLines = 2,
                    style = MaterialTheme.typography.headlineMedium,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.size(MaterialTheme.dimens.minButtonHeight))
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            BlockTitle(stringResource(R.string.DataSources))
            Column(
                modifier = Modifier
                    .padding(
                        top = MaterialTheme.spacing.extraSmall,
                        start = leftInset + MaterialTheme.spacing.screenHorizontal,
                        end = rightInset + MaterialTheme.spacing.screenHorizontal
                    )
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                AboutAppBlock(
                    title = "AniLibria",
                    ico = ImageData.Url("https://www.google.com/s2/favicons?domain=aniliberty.top&sz=128"),
                ) {
                    uriHandler.openUri("https://anilibria.top")
                }

                AboutAppBlock(
                    title = "MangaDex",
                    ico = ImageData.Url("https://www.google.com/s2/favicons?domain=mangadex.org&sz=128"),
                ) {
                    uriHandler.openUri("https://mangadex.org")
                }

                AboutAppBlock(
                    title = "MusicBrainz",
                    ico = ImageData.Url("https://www.google.com/s2/favicons?domain=musicbrainz.org&sz=128"),
                ) {
                    uriHandler.openUri("https://musicbrainz.org")
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = topInset + MaterialTheme.spacing.screenHorizontal,
                    start = MaterialTheme.spacing.screenHorizontal + leftInset
                )
        ) {
            CloseButton(
                onClick = {
                    focusManager.clearFocus()
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onClose()
                }
            )
        }
    }
}

@Composable
private fun AboutAppBlock(
    title: String,
    ico: ImageData,
    onClick: () -> Unit
) {
    SettingsBlock(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .clickable {
                    onClick()
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                AppAsyncImage(
                    imageData = ico,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(MaterialTheme.dimens.minButtonHeight)
                        .clip(CircleShape)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BlockTitle(
    title: String
) {
    val density = LocalDensity.current

    val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)
    val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)

    val leftInset = with(density) { leftInsetPx.toDp() }
    val rightInset = with(density) { rightInsetPx.toDp() }

    Text(
        modifier = Modifier.padding(
            start = leftInset + MaterialTheme.spacing.screenHorizontal,
            end = rightInset + MaterialTheme.spacing.screenHorizontal
        ),
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        overflow = TextOverflow.Ellipsis,
    )
}
