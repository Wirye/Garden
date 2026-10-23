package com.example.garden.ui.components

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import android.provider.OpenableColumns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.garden.R
import com.example.garden.database.entities.ImageData
import com.example.garden.database.entities.LinkData
import com.example.garden.database.entities.VoiceInfo
import com.example.garden.ui.components.icons.AddIco
import com.example.garden.ui.components.icons.DeleteIco
import com.example.garden.ui.screens.LocalHazeLayers
import com.example.garden.ui.screens.PageWithSearchItem
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.dataForModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
data class VariantDraft(
    val id: Long,
    val voice: VoiceInfo,
    val link: LinkData
) : Parcelable

@Parcelize
data class EpisodeDraft(
    override val id: Long,
    val number: Int?,
    override val name: String,
    override val image: ImageData?,
    val variants: List<VariantDraft>
) : PageWithSearchItem {
    override fun copyWithName(newName: String) = copy(name = newName)
    override fun copyWithImage(newImage: ImageData?) = copy(image = newImage)

    companion object {
        val Empty = EpisodeDraft(id = 0L, number = null, name = "", image = null, variants = emptyList())
    }
}

data class PickedFile(val uri: String, val fileName: String, val length: Long)

@Composable
fun EpisodeRow(
    item: EpisodeDraft,
    hasNumberError: Boolean,
    onNumberChange: (Int?) -> Unit,
    onNumberCommit: () -> Unit,
    onNameChange: (String) -> Unit,
    onPreviewChange: (ImageData?) -> Unit,
    onVoiceChange: (variantId: Long, VoiceInfo) -> Unit,
    onDeleteVariant: (variantId: Long) -> Unit,
    onAddVariant: () -> Unit,
    onDelete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    val currentOnNumberChange by rememberUpdatedState(onNumberChange)
    val currentOnNameChange by rememberUpdatedState(onNameChange)

    val numberState = rememberTextFieldState(item.number?.toString().orEmpty())
    val nameState = rememberTextFieldState(item.name)

    LaunchedEffect(numberState) {
        snapshotFlow { numberState.text.toString() }
            .distinctUntilChanged()
            .collect { currentOnNumberChange(it.toIntOrNull()) }
    }
    LaunchedEffect(nameState) {
        snapshotFlow { nameState.text.toString() }
            .distinctUntilChanged()
            .collect { currentOnNameChange(it) }
    }

    var isPreviewDialogExpanded by remember { mutableStateOf(false) }
    val pickPreview = rememberFilePicker(mimeTypes = arrayOf("image/*")) { uri ->
        if (uri != null) onPreviewChange(ImageData.Device(uri.toString()))
    }

    SmartFilePicker(
        expanded = isPreviewDialogExpanded,
        onDismiss = { isPreviewDialogExpanded = false },
        onFileDelete = {
            isPreviewDialogExpanded = false
            onPreviewChange(null)
        },
        onFileChange = {
            isPreviewDialogExpanded = false
            pickPreview()
        },
        hazeState = LocalHazeLayers.current.mainScreen
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            AsyncImageWithAddPlaceholder(
                model = item.image?.dataForModel(),
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        isPreviewDialogExpanded = true
                    },
                shape = MaterialTheme.shapes.small
            )

            OutlinedTextField(
                state = numberState,
                modifier = Modifier
                    .width(MaterialTheme.dimens.minButtonHeight)
                    .onFocusChanged { if (!it.isFocused) onNumberCommit() },
                shape = MaterialTheme.shapes.small,
                isError = hasNumberError,
                inputTransformation = digitsOnly,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                onKeyboardAction = { _ ->
                    onNumberCommit()
                    focusManager.clearFocus()
                },
                lineLimits = TextFieldLineLimits.SingleLine,
                placeholder = { Text(stringResource(R.string.episodeNumberHint)) }
            )

            OutlinedTextField(
                state = nameState,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small,
                lineLimits = TextFieldLineLimits.SingleLine,
                placeholder = { Text(stringResource(R.string.title), maxLines = 1) }
            )

            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                onDelete()
            }) {
                Icon(
                    imageVector = DeleteIco,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                )
            }
        }

        item.variants.forEach { variant ->
            key(variant.id) {
                VariantRow(
                    variant = variant,
                    canDelete = item.variants.size > 1,
                    onVoiceChange = { onVoiceChange(variant.id, it) },
                    onDelete = { onDeleteVariant(variant.id) }
                )
            }
        }

        TextButton(onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
            focusManager.clearFocus()
            onAddVariant()
        }) {
            Icon(
                imageVector = AddIco,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
            )
            Text(
                text = stringResource(R.string.addVoice),
                modifier = Modifier.padding(start = MaterialTheme.spacing.small)
            )
        }
    }
}

@Composable
private fun VariantRow(
    variant: VariantDraft,
    canDelete: Boolean,
    onVoiceChange: (VoiceInfo) -> Unit,
    onDelete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val voice by rememberUpdatedState(variant.voice)
    val currentOnVoiceChange by rememberUpdatedState(onVoiceChange)

    val nameState = rememberTextFieldState(if (variant.voice.isAniLibria) "" else variant.voice.name)

    LaunchedEffect(nameState) {
        snapshotFlow { nameState.text.toString() }
            .distinctUntilChanged()
            .collect { text ->
                if (!voice.isAniLibria && text != voice.name) {
                    currentOnVoiceChange(voice.copy(name = text))
                }
            }
    }

    val fileLabel = (variant.link as? LinkData.Device)
        ?.let { Uri.decode(it.path).substringAfterLast('/') }
        .orEmpty()

    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    FilterChip(
                        selected = voice.isAniLibria,
                        onClick = { onVoiceChange(VoiceInfo(name = "AniLibria", isAniLibria = true)) },
                        label = { Text(stringResource(R.string.AniLibria)) }
                    )
                    FilterChip(
                        selected = !voice.isAniLibria,
                        onClick = {
                            onVoiceChange(VoiceInfo(name = nameState.text.toString(), isAniLibria = false))
                        },
                        label = { Text(stringResource(R.string.voiceOther)) }
                    )
                }
                if (canDelete) {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        onDelete()
                    }) {
                        Icon(
                            imageVector = DeleteIco,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                        )
                    }
                }
            }

            if (!voice.isAniLibria) {
                OutlinedTextField(
                    state = nameState,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    placeholder = { Text(stringResource(R.string.voiceUnknown)) }
                )
            }

            if (fileLabel.isNotEmpty()) {
                Text(
                    text = fileLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun newDraftId(): Long = Random.nextLong()

fun MutableList<PageWithSearchItem>.updateEpisode(id: Long, transform: (EpisodeDraft) -> EpisodeDraft) {
    val index = indexOfFirst { it.id == id }
    val old = getOrNull(index) as? EpisodeDraft ?: return
    val new = transform(old)
    if (new != old) this[index] = new
}

fun MutableList<PageWithSearchItem>.sortEpisodesByNumber() {
    sortWith(compareBy(nullsLast()) { (it as? EpisodeDraft)?.number })
}

fun MutableList<PageWithSearchItem>.addEpisodeFiles(files: List<PickedFile>, voice: VoiceInfo) {
    files.sortedBy { it.fileName }.forEach { file ->
        val number = guessEpisodeNumber(file.fileName)
            ?: ((filterIsInstance<EpisodeDraft>().mapNotNull { it.number }.maxOrNull() ?: 0) + 1)
        val variant = file.toVariant(voice)
        val existing = filterIsInstance<EpisodeDraft>().firstOrNull { it.number == number }
        if (existing != null) {
            updateEpisode(existing.id) { it.copy(variants = it.variants + variant) }
        } else {
            add(EpisodeDraft(newDraftId(), number, "", null, listOf(variant)))
        }
    }
    sortEpisodesByNumber()
}

fun MutableList<PageWithSearchItem>.addVariants(episodeId: Long, files: List<PickedFile>, voice: VoiceInfo) {
    updateEpisode(episodeId) { it.copy(variants = it.variants + files.map { f -> f.toVariant(voice) }) }
}

fun MutableList<PageWithSearchItem>.setVoice(episodeId: Long, variantId: Long, voice: VoiceInfo) {
    updateEpisode(episodeId) { ep ->
        ep.copy(variants = ep.variants.map { if (it.id == variantId) it.copy(voice = voice) else it })
    }
}

fun MutableList<PageWithSearchItem>.removeVariant(episodeId: Long, variantId: Long) {
    updateEpisode(episodeId) { ep -> ep.copy(variants = ep.variants.filterNot { it.id == variantId }) }
}

fun List<PageWithSearchItem>.duplicatedEpisodeNumbers(): Set<Int> =
    filterIsInstance<EpisodeDraft>()
        .mapNotNull { it.number }
        .groupingBy { it }.eachCount()
        .filterValues { it > 1 }.keys

fun List<PageWithSearchItem>.episodesAreValid(): Boolean =
    filterIsInstance<EpisodeDraft>().all { it.number != null && it.variants.isNotEmpty() } &&
            duplicatedEpisodeNumbers().isEmpty()

private fun PickedFile.toVariant(voice: VoiceInfo) =
    VariantDraft(id = newDraftId(), voice = voice, link = LinkData.Device(path = uri))

private val markedNumber =
    Regex("""(?i)(?:\bep(?:isode)?|\be|s\d{1,2}e|серия|эпизод)[\s._-]*(\d{1,3})(?!\d)""")

private val looseNumber = Regex("""(?<![\p{L}\d])(\d{1,3})(?![\p{L}\d])""")

fun guessEpisodeNumber(fileName: String): Int? {
    val name = fileName.substringBeforeLast('.')
    markedNumber.find(name)?.groupValues?.get(1)?.toIntOrNull()?.let { return it }
    return looseNumber.findAll(name).lastOrNull()?.groupValues?.get(1)?.toIntOrNull()
}

fun Context.displayName(uri: Uri): String =
    contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
        if (c.moveToFirst()) c.getString(0) else null
    } ?: uri.lastPathSegment.orEmpty()

private val digitsOnly = InputTransformation {
    if (!asCharSequence().all { it.isDigit() }) revertAllChanges()
}
