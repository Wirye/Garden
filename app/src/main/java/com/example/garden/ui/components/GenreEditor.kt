package com.example.garden.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.garden.R
import com.example.garden.database.Genre
import com.example.garden.database.GenreAge
import com.example.garden.database.GenreEpisodes
import com.example.garden.database.GenreSezon
import com.example.garden.database.GenreYear
import com.example.garden.database.GridGenreItem
import com.example.garden.database.MusicGenre
import com.example.garden.database.YearSezon
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.clearFocus
import com.example.garden.utils.getAllGenresOfSameType
import com.example.garden.utils.getGenreClass
import kotlinx.coroutines.launch

enum class GenreEditorGenresType {
    ANIME, MUSIC
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GenreEditor(
    onDismiss: () -> Unit,
    initList: List<GridGenreItem>,
    genreType: GenreEditorGenresType,
    onApply: (List<GridGenreItem>) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val genres = remember(initList, genreType) {
        getAllGenresOfSameType(
            when (genreType) {
                GenreEditorGenresType.ANIME -> listOf(Genre.Drama)
                GenreEditorGenresType.MUSIC -> listOf(MusicGenre.Classical)
            }
        )
    }

    val extraInfoClasses = remember(genreType) {
        when (genreType) {
            GenreEditorGenresType.ANIME -> listOf(
                GenreAge::class.java, GenreYear::class.java,
                GenreSezon::class.java, GenreEpisodes::class.java
            )

            GenreEditorGenresType.MUSIC -> listOf(GenreAge::class.java, GenreYear::class.java)
        }
    }

    val selectedGenres = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { mutableStateListOf<GridGenreItem>().apply { addAll(it) } }
        )
    ) {
        mutableStateListOf<GridGenreItem>().apply {
            addAll(initList.filter { it.getGenreClass() == (if (genreType == GenreEditorGenresType.ANIME)
                Genre::class.java else MusicGenre::class.java) })
        }
    }

    val selectedExtraInfo = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { mutableStateListOf<GridGenreItem>().apply { addAll(it) } }
        )
    ) {
        mutableStateListOf<GridGenreItem>().apply {
            addAll(initList.filter { it.getGenreClass() in extraInfoClasses })
        }
    }

    val initialAge = remember(selectedExtraInfo) {
        (selectedExtraInfo.firstOrNull { it.getGenreClass() == GenreAge::class.java } as? GenreAge)?.age?.toString() ?: ""
    }
    val initialYear = remember(selectedExtraInfo) {
        (selectedExtraInfo.firstOrNull { it.getGenreClass() == GenreYear::class.java } as? GenreYear)?.year?.toString() ?: ""
    }
    val initialEpisodes = remember(selectedExtraInfo) {
        selectedExtraInfo.any { it.getGenreClass() == GenreEpisodes::class.java }
    }
    val initialSezon = remember(initList) {
        (selectedExtraInfo.firstOrNull { it.getGenreClass() == GenreSezon::class.java } as? GenreSezon)?.sezon
    }

    var ageInput by rememberSaveable { mutableStateOf(initialAge) }
    var yearInput by rememberSaveable { mutableStateOf(initialYear) }
    var episodesChecked by rememberSaveable { mutableStateOf(initialEpisodes) }
    var selectedSezon by rememberSaveable { mutableStateOf(initialSezon) }

    fun updateExtraInfo() {
        selectedExtraInfo.clear()

        val age = ageInput.filter { it.isDigit() }.toIntOrNull()
        if (age != null) selectedExtraInfo.add(GenreAge(age = age))

        val year = yearInput.filter { it.isDigit() }.toIntOrNull()
        if (year != null) selectedExtraInfo.add(GenreYear(year = year))

        if (episodesChecked && genreType == GenreEditorGenresType.ANIME) {
            selectedExtraInfo.add(GenreEpisodes())
        }

        if (selectedSezon != null && genreType == GenreEditorGenresType.ANIME) {
            selectedExtraInfo.add(GenreSezon(sezon = selectedSezon))
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val animateAndDismiss: () -> Unit = {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.screenHorizontal)
                .clearFocus(focusManager)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.selectGenres_action),
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.andConfigureAdditionalInformation),
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.medium))

            Column(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .verticalScroll(rememberScrollState())
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = ageInput,
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { it.isDigit() }
                            if (filtered.length <= 2) {
                                ageInput = filtered
                                updateExtraInfo()
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        label = { Text(stringResource(R.string.Age)) },
                        visualTransformation = AgeVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = yearInput,
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { it.isDigit() }
                            if (filtered.length <= 4) {
                                yearInput = filtered
                                updateExtraInfo()
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        label = { Text(stringResource(R.string.Year)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (genreType == GenreEditorGenresType.ANIME) {
                        FilterChip(
                            modifier = Modifier.fillMaxWidth(),
                            selected = episodesChecked,
                            onClick = {
                                if (episodesChecked) {
                                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                                } else {
                                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                                }
                                episodesChecked = !episodesChecked
                                updateExtraInfo()
                            },
                            trailingIcon = {
                                Switch(
                                    checked = episodesChecked,
                                    onCheckedChange = {
                                        if (it) {
                                            haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                                        } else {
                                            haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                                        }
                                        episodesChecked = it
                                        updateExtraInfo()
                                    },
                                    modifier = Modifier.scale(0.7f)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(R.string.EpisodesAnount),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        )

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.Sezons),
                            style = MaterialTheme.typography.labelLarge
                        )

                        YearSezon.entries.forEach { sezon ->
                            val isSelected = selectedSezon == sezon
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                                    } else {
                                        haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                                    }
                                    selectedSezon = if (isSelected) null else sezon
                                    updateExtraInfo()
                                },
                                label = {
                                    Text(
                                        text = stringResource(sezon.displayNameId),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            )
                        }

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.Genres),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    genres.forEach { genre ->
                        val isSelected = selectedGenres.contains(genre)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                                } else {
                                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                                }
                                if (isSelected) selectedGenres.remove(genre)
                                else selectedGenres.add(genre)
                            },
                            label = {
                                Text(
                                    stringResource(genre.displayNameId),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    animateAndDismiss()
                    onApply(selectedGenres + selectedExtraInfo)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(vertical = MaterialTheme.spacing.small)
            ) {
                Text(
                    text = stringResource(R.string.apply),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

private class AgeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val transformedText = "$originalText+"

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset.coerceIn(0, originalText.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return if (offset >= transformedText.length) originalText.length else offset
            }
        }

        return TransformedText(AnnotatedString(transformedText), offsetMapping)
    }
}
