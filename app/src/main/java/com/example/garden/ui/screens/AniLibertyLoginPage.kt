package com.example.garden.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import com.example.garden.LocalCustomColors
import com.example.garden.R
import com.example.garden.ui.components.icons.CloseIco
import com.example.garden.ui.components.icons.EyeIco
import com.example.garden.ui.components.icons.EyeOffIco
import com.example.garden.ui.components.icons.HelpIco
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus
import com.example.garden.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AniLibertyLoginPage(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    onSuccessAuth: (sessionId: String) -> Unit,
    onClose: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    val density = LocalDensity.current
    val topInset = with(density) { WindowInsets.safeDrawing.getTop(density).toDp() }
    val rightInset = with(density) {
        WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current).toDp()
    }
    val leftInset = with(density) {
        WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current).toDp()
    }

    val coroutineScope = rememberCoroutineScope()

    var loginText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val performLogin = {
        if (loginText.isBlank() || passwordText.isBlank()) {
            errorMessage = "Заполните все поля"
        } else {
            focusManager.clearFocus()
            isLoading = true
            errorMessage = null

            coroutineScope.launch(Dispatchers.IO) {
                val result = authViewModel.executeAniLibertyLogin(loginText.trim(), passwordText)
                withContext(Dispatchers.Main) {
                    isLoading = false
                    result.onSuccess { sessionKey ->
                        onSuccessAuth(sessionKey)
                    }.onFailure { error ->
                        errorMessage = error.localizedMessage ?: "Ошибка авторизации"
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
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
            horizontalArrangement = Arrangement.SpaceBetween
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

            Text(
                modifier = Modifier.weight(1f, fill = false),
                text = stringResource(R.string.aniLibertyLogin),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                softWrap = false
            )

            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                focusManager.clearFocus()
            }) {
                Icon(
                    imageVector = HelpIco,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = loginText,
                onValueChange = { loginText = it },
                label = { Text("Логин или Email") },
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentType = ContentType.Username },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            OutlinedTextField(
                value = passwordText,
                onValueChange = { passwordText = it },
                label = { Text("Пароль") },
                singleLine = true,
                enabled = !isLoading,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { performLogin() }
                ),
                shape = MaterialTheme.shapes.medium,
                trailingIcon = {
                    if (passwordText.isNotEmpty()) {
                        IconButton(onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            isPasswordVisible = !isPasswordVisible
                        }) {
                            Icon(
                                imageVector = if (isPasswordVisible) EyeOffIco else EyeIco,
                                modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                                contentDescription = null
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentType = ContentType.Password }
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    focusManager.clearFocus()
                    performLogin()
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = MaterialTheme.dimens.strokeThick
                    )
                } else {
                    Text("Войти")
                }
            }
        }
    }
}
