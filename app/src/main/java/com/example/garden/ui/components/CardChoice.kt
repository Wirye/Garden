package com.example.garden.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.garden.R
import com.example.garden.database.ElementType
import com.example.garden.database.ObjectData
import com.example.garden.ui.components.icons.SearchIco
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.bottomSheetAnimateAndDismiss
import com.example.garden.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardChoice(
    mainViewModel: MainViewModel,
    isSingleChoice: Boolean,
    cardTypes: List<ElementType>,
    initCardsList: List<Long>,
    onDismiss: () -> Unit,
    onApply: (List<Long>) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val selectedCardIds = rememberSaveable { initCardsList.toMutableStateList() }

    val coroutineScope = rememberCoroutineScope()

    val searchState = rememberTextFieldState(initialText = "")

    val pagingCards = mainViewModel.searchCardsFlow.collectAsLazyPagingItems()

    LaunchedEffect(searchState.text.toString()) {
        val text = searchState.text.toString()
        mainViewModel.searchCards(text, cardTypes)
    }

    val animateAndDismiss: () -> Unit = {
        bottomSheetAnimateAndDismiss(
            coroutineScope = coroutineScope,
            sheetState = sheetState,
            onDismiss = {
                mainViewModel.clearCardsSearch()
                onDismiss()
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = {
            mainViewModel.clearCardsSearch()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blockGestures()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaterialTheme.spacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(if (isSingleChoice) R.string.SelectCard_action else R.string.SelectCards_action),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    state = searchState,
                    modifier = Modifier.fillMaxWidth(),
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(bottom = MaterialTheme.dimens.minButtonHeight + MaterialTheme.spacing.medium)
                    ) {
                        items(
                            count = pagingCards.itemCount,
                            key = pagingCards.itemKey { it.id }
                        ) { index ->
                            val entity = pagingCards[index]
                            if (entity != null) {
                                val card =
                                    entity.info.injectObjectEntityData(entity) as ObjectData.Card
                                val isSelected = entity.id in selectedCardIds

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                                            if (isSingleChoice) {
                                                if (!isSelected) {
                                                    selectedCardIds.clear()
                                                    selectedCardIds.add(entity.id)
                                                }
                                            } else {
                                                if (isSelected) {
                                                    selectedCardIds.remove(entity.id)
                                                } else {
                                                    selectedCardIds.add(entity.id)
                                                }
                                            }
                                        }
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.secondaryContainer
                                            else MaterialTheme.colorScheme.surfaceContainerHigh
                                        )
                                ) {
                                    CardPreview(isSupportDragging = false, card = card)
                                }
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(horizontal = MaterialTheme.spacing.screenHorizontal)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        animateAndDismiss()
                        onApply(selectedCardIds)
                    },
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(
                        text = stringResource(R.string.Choice) + " (${selectedCardIds.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
