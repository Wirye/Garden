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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.garden.Layer
import com.example.garden.LocalCustomColors
import com.example.garden.R
import com.example.garden.database.ImageData
import com.example.garden.ui.components.AppAsyncImage
import com.example.garden.ui.components.icons.CloseIco
import com.example.garden.ui.components.icons.LoginIco
import com.example.garden.ui.components.icons.LogoutIco
import com.example.garden.ui.components.icons.SearchIco
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus
import com.example.garden.viewmodel.AuthViewModel
import com.example.garden.viewmodel.LayersViewModel

@Composable
fun AppSettings(
    authViewModel: AuthViewModel,
    layersViewModel: LayersViewModel,
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

    val isGoogleAuthorized = uiState.isGoogleAuthorized
    val googleUserName = uiState.googleNickName
    val linkToGoogleAvatar = uiState.googleAvatarUrl
    val googleEmail = uiState.googleEmail

    val isAniLibertyAuthorized = uiState.isAniLibertyAuthorized
    val aniLibertyUserName = uiState.aniLibertyNickName
    val linkToAniLibertyAvatar = uiState.aniLibertyAvatarUrl

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
                    imageVector = CloseIco,
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
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0f),
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
                        imageVector = SearchIco,
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
                            MaterialTheme.shapes.extraLarge.copy(
                                bottomStart = CornerSize(0.dp),
                                bottomEnd = CornerSize(0.dp)
                            )
                        } else MaterialTheme.shapes.extraLarge
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    }
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
                        AppAsyncImage(
                            imageData = ImageData.Url("https://www.google.com/s2/favicons?domain=google.com&sz=128"),
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
                            imageVector = LoginIco,
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
                        title = stringResource(R.string.LogoutFromGoogle),
                        message = stringResource(R.string.PersonalizedContentWillBecomeUnavailable),
                        onConfirm = { isLogoutAskExpanded = false },
                        onDismiss = { isLogoutAskExpanded = false }
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(
                            MaterialTheme.shapes.extraLarge.copy(
                                topStart = CornerSize(0.dp),
                                topEnd = CornerSize(0.dp)
                            )
                        )
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            isLogoutAskExpanded = true
                        }
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
                            AppAsyncImage(
                                imageData = if (linkToGoogleAvatar != null) ImageData.Url(
                                    linkToGoogleAvatar
                                ) else null,
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
                            imageVector = LogoutIco,
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
                            MaterialTheme.shapes.extraLarge.copy(
                                bottomStart = CornerSize(0.dp),
                                bottomEnd = CornerSize(0.dp)
                            )
                        } else MaterialTheme.shapes.extraLarge
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        if (!isAniLibertyAuthorized) {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            layersViewModel.openLayer(
                                Layer.AniLibertyLoginPage()
                            )
                        }
                    }
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
                        AppAsyncImage(
                            imageData = ImageData.Url("https://www.google.com/s2/favicons?domain=aniliberty.top&sz=128"),
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
                            imageVector = LoginIco,
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
                        title = stringResource(R.string.LogoutFromAniLiberty),
                        message = stringResource(R.string.PersonalizedContentWillBecomeUnavailable),
                        onConfirm = {
                            isLogoutAskExpanded = false
                            authViewModel.logoutAniLiberty()
                        },
                        onDismiss = { isLogoutAskExpanded = false }
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(
                            MaterialTheme.shapes.extraLarge.copy(
                                topStart = CornerSize(0.dp),
                                topEnd = CornerSize(0.dp)
                            )
                        )
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            isLogoutAskExpanded = true
                        }
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
                            AppAsyncImage(
                                imageData = if (linkToAniLibertyAvatar != null) ImageData.Url(
                                    linkToAniLibertyAvatar
                                ) else null,
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
                            imageVector = LogoutIco,
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
    val haptic = LocalHapticFeedback.current

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
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onConfirm()
                }
            ) {
                Text(text = "Выйти")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onDismiss()
                }
            ) {
                Text(text = "Отмена")
            }
        }
    )
}
