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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.garden.LocalCustomColors
import com.example.garden.R
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus
import com.example.garden.viewmodel.AuthViewModel

@Composable
fun AppSettings(
    authViewModel: AuthViewModel,
    onClose: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current

    val haptic = LocalHapticFeedback.current

    val density = LocalDensity.current
    val topInsetPx = WindowInsets.safeDrawing.getTop(density)
    val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)
    val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)

    val topInset = with(density) { topInsetPx.toDp() }
    val rightInset = with(density) { rightInsetPx.toDp() }
    val leftInset = with(density) { leftInsetPx.toDp() }

    val isGoogleAuthorized by remember { mutableStateOf(uiState.isGoogleAuthorized) }
    val googleUserName by remember { mutableStateOf(uiState.googleNickName) }
    val linkToGoogleAvatar: String? by remember { mutableStateOf(uiState.googleAvatarUrl) }
    val googleEmail: String? by remember { mutableStateOf(uiState.googleEmail) }

    val isAniLibertyAuthorized by remember { mutableStateOf(uiState.isAniLibertyAuthorized) }
    val aniLibertyUserName by remember { mutableStateOf(uiState.aniLibertyNickName) }
    val linkToAniLibertyAvatar: String? by remember { mutableStateOf(uiState.aniLibertyAvatarUrl) }

    val searchState = rememberTextFieldState(initialText = "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = topInset + MaterialTheme.spacing.screenHorizontal,
                start = leftInset + MaterialTheme.spacing.screenHorizontal,
                end = rightInset + MaterialTheme.spacing.screenHorizontal
            )
            .blockGestures()
            .clearFocus(focusManager),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            FilledIconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    focusManager.clearFocus()
                    onClose()
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = LocalCustomColors.current.closeButton,
                    contentColor = LocalCustomColors.current.onCloseButton
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    painter = painterResource(R.drawable.close_ico),
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                )
            }

            OutlinedTextField(
                state = searchState,
                modifier = Modifier
                    .weight(1f, fill = true)
                    .fillMaxWidth(),
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.Search),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search_ico),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                    )
                },
                lineLimits = TextFieldLineLimits.SingleLine
            )
        }

        Column(
            modifier = Modifier.weight(1f, fill = true),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        if (isGoogleAuthorized) {
                            MaterialTheme.shapes.medium.copy(
                                bottomStart = CornerSize(0.dp),
                                bottomEnd = CornerSize(0.dp)
                            )
                        } else MaterialTheme.shapes.medium
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {}
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://www.google.com/s2/favicons?domain=google.com&sz=128")
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(MaterialTheme.dimens.minButtonHeight)
                                .clip(CircleShape)
                        )

                        Text(
                            text = stringResource(R.string.Account),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!isGoogleAuthorized) {
                        Icon(
                            painter = painterResource(R.drawable.login_ico),
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            if (isGoogleAuthorized) {
                var isLogoutAskExpanded by remember { mutableStateOf(false) }
                if (isLogoutAskExpanded) {
                    ConfirmLogoutDialog(
                        title = "Выйти из Google?",
                        message = "Персонализированный контент станет недоступен",
                        onConfirm = { isLogoutAskExpanded = false },
                        onDismiss = { isLogoutAskExpanded = false }
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(
                            MaterialTheme.shapes.medium.copy(
                                topStart = CornerSize(0.dp),
                                topEnd = CornerSize(0.dp)
                            )
                        )
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { isLogoutAskExpanded = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(linkToGoogleAvatar)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(MaterialTheme.dimens.minButtonHeight)
                                    .clip(CircleShape)
                            )

                            Column {
                                Text(
                                    text = googleUserName ?: stringResource(R.string.withoutName),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )

                                Text(
                                    text = googleEmail ?: stringResource(R.string.withoutEmail),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            painter = painterResource(R.drawable.logout_ico),
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.medium)
            )

            Box(
                modifier = Modifier
                    .clip(
                        if (isAniLibertyAuthorized) {
                            MaterialTheme.shapes.medium.copy(
                                bottomStart = CornerSize(0.dp),
                                bottomEnd = CornerSize(0.dp)
                            )
                        } else MaterialTheme.shapes.medium
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {}
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://www.google.com/s2/favicons?domain=aniliberty.top&sz=128")
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(MaterialTheme.dimens.minButtonHeight)
                                .clip(CircleShape)
                        )

                        Text(
                            text = stringResource(R.string.Account),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!isAniLibertyAuthorized) {
                        Icon(
                            painter = painterResource(R.drawable.login_ico),
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            if (isAniLibertyAuthorized) {
                var isLogoutAskExpanded by remember { mutableStateOf(false) }
                if (isLogoutAskExpanded) {
                    ConfirmLogoutDialog(
                        title = "Выйти из AniLiberty?",
                        message = "Вы потеряете доступ к рекомендациям и плейлистам YouTube Music.",
                        onConfirm = {
                            isLogoutAskExpanded = false
                        },
                        onDismiss = { isLogoutAskExpanded = false }
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(
                            MaterialTheme.shapes.medium.copy(
                                topStart = CornerSize(0.dp),
                                topEnd = CornerSize(0.dp)
                            )
                        )
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { isLogoutAskExpanded = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(linkToAniLibertyAvatar)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(MaterialTheme.dimens.minButtonHeight)
                                    .clip(CircleShape)
                            )

                            Text(
                                text = aniLibertyUserName ?: stringResource(R.string.withoutName),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            painter = painterResource(R.drawable.logout_ico),
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmLogoutDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = "Выйти")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Отмена")
            }
        }
    )
}
