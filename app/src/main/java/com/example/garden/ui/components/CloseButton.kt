package com.example.garden.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.garden.LocalCustomColors
import com.example.garden.ui.components.icons.CloseIco
import com.example.garden.ui.theme.dimens

@Composable
fun CloseButton(
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = LocalCustomColors.current.closeButton,
            contentColor = LocalCustomColors.current.onCloseButton
        ), shape = MaterialTheme.shapes.small
    ) {
        Icon(
            imageVector = CloseIco,
            contentDescription = null,
            modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
        )
    }
}
