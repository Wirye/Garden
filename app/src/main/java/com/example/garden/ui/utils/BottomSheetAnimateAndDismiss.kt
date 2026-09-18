package com.example.garden.ui.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
fun bottomSheetAnimateAndDismiss(coroutineScope: CoroutineScope, sheetState: SheetState, onDismiss: () -> Unit) {
    coroutineScope.launch {
        sheetState.hide()
    }.invokeOnCompletion {
        if (!sheetState.isVisible) {
            onDismiss()
        }
    }
}