package com.example.garden.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun DialogWithChoice(
    title: String,
    message: String,
    confirmButtonTitle: String,
    dismissButtonTitle: String,
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
                Text(text = confirmButtonTitle)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onDismiss()
                }
            ) {
                Text(text = dismissButtonTitle)
            }
        }
    )
}
