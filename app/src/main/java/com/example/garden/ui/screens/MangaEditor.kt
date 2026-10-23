package com.example.garden.ui.screens

import android.annotation.SuppressLint
import android.os.Parcelable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import com.example.garden.R
import com.example.garden.database.entities.ImageData
import com.example.garden.ui.components.AsyncImageWithAddPlaceholder
import com.example.garden.ui.components.SmartFilePicker
import com.example.garden.ui.components.icons.DeleteIco
import com.example.garden.ui.components.icons.DragHandleIco
import com.example.garden.ui.components.icons.EditIco
import com.example.garden.ui.components.newDraftId
import com.example.garden.ui.components.rememberFilePicker
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.dataForModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.parcelize.Parcelize

private val digitsOnlyInput = InputTransformation {
    if (!asCharSequence().all { it.isDigit() }) revertAllChanges()
}

@Composable
fun ChapterRow(
    item: ChapterDraft,
    hasNumberError: Boolean,
    onNumberChange: (Int?) -> Unit,
    onNumberCommit: () -> Unit,
    onNameChange: (String) -> Unit,
    onEditPages: () -> Unit,
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

    val languages = item.pages
        .flatMap { page -> page.variants.map { it.language } }
        .distinct()
        .joinToString(", ") { it.uppercase() }
    val summary = stringResource(R.string.pagesСount, item.pages.size) +
        if (languages.isNotEmpty()) " · $languages" else ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        OutlinedTextField(
            state = numberState,
            modifier = Modifier
                .width(76.dp)
                .onFocusChanged { if (!it.isFocused) onNumberCommit() },
            shape = MaterialTheme.shapes.small,
            isError = hasNumberError,
            inputTransformation = digitsOnlyInput,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            onKeyboardAction = { _ ->
                onNumberCommit()
                focusManager.clearFocus()
            },
            lineLimits = TextFieldLineLimits.SingleLine,
            placeholder = { Text(stringResource(R.string.episodeNumberHint)) }
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            OutlinedTextField(
                state = nameState,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                lineLimits = TextFieldLineLimits.SingleLine,
                placeholder = { Text(stringResource(R.string.title)) }
            )
            Text(
                text = summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
            focusManager.clearFocus()
            onEditPages()
        }) {
            Icon(
                imageVector = EditIco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
            )
        }

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
}

@Composable
fun PageRow(
    page: PageDraft,
    position: Int,
    language: String,
    onImageChange: (ImageData?) -> Unit,
    onDelete: () -> Unit,
    @SuppressLint("ModifierParameter") dragModifier: Modifier
) {
    val haptic = LocalHapticFeedback.current

    val image = page.variants.firstOrNull { it.language == language }?.content
    val languages = page.variants.joinToString(" · ") { it.language.uppercase() }

    val pickImage = rememberFilePicker(mimeTypes = arrayOf("image/*")) { uri ->
        if (uri != null) onImageChange(ImageData.Device(uri.toString()))
    }

    var isDialogExpanded by remember { mutableStateOf(false) }
    SmartFilePicker(
        expanded = isDialogExpanded,
        onDismiss = { isDialogExpanded = false },
        onFileDelete = {
            isDialogExpanded = false
            onImageChange(null)          // убирает только выбранный язык этой страницы
        },
        onFileChange = {
            isDialogExpanded = false
            pickImage()
        },
        hazeState = LocalHazeLayers.current.mainScreen
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Text(
                text = "#$position",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            AsyncImageWithAddPlaceholder(
                model = image?.dataForModel(),
                modifier = Modifier
                    .size(width = 48.dp, height = 64.dp)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        // нет картинки на этом языке — сразу выбираем файл для этого слота
                        if (image == null) pickImage() else isDialogExpanded = true
                    },
                shape = MaterialTheme.shapes.small
            )
            Text(
                text = languages,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            Icon(
                imageVector = DragHandleIco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = dragModifier
                    .padding(MaterialTheme.spacing.small)
                    .size(MaterialTheme.dimens.iconMedium)
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
    }
}

@Composable
fun AddLanguageDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val state = rememberTextFieldState()
    fun getLanguageCode() = state.text.toString().trim().lowercase().filter { it.isLetter() || it == '-' }.take(12)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.languageAdd)) },
        text = {
            OutlinedTextField(
                state = state,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                onKeyboardAction = { _ -> if (getLanguageCode().isNotEmpty()) onConfirm(getLanguageCode()) },
                placeholder = { Text(stringResource(R.string.languageCodeHint)) }
            )
        },
        confirmButton = {
            TextButton(onClick = { if (getLanguageCode().isNotEmpty()) onConfirm(getLanguageCode()) }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}

@Parcelize
data class PageVariantDraft(
    val language: String,       // "ru", "en", ... всегда lowercase
    val content: ImageData
) : Parcelable

@Parcelize
data class PageDraft(
    override val id: Long,
    val variants: List<PageVariantDraft>
) : PageWithSearchItem {
    companion object {
        val Empty = PageDraft(id = 0L, variants = emptyList())
    }
}

@Parcelize
data class ChapterDraft(
    override val id: Long,
    val number: Int?,           // null = ещё не заполнен (будет ошибка при сохранении)
    override val name: String,
    val pages: List<PageDraft>
) : PageWithSearchItem {
    override fun copyWithName(newName: String) = copy(name = newName)

    companion object {
        val Empty = ChapterDraft(id = 0L, number = null, name = "", pages = emptyList())
    }
}

fun MutableList<PageWithSearchItem>.updateChapter(id: Long, transform: (ChapterDraft) -> ChapterDraft) {
    val index = indexOfFirst { it.id == id }
    val old = getOrNull(index) as? ChapterDraft ?: return
    val new = transform(old)
    if (new != old) this[index] = new
}

fun MutableList<PageWithSearchItem>.sortChaptersByNumber() {
    sortWith(compareBy(nullsLast()) { (it as? ChapterDraft)?.number })
}

fun MutableList<PageWithSearchItem>.addChapter() {
    val next = (filterIsInstance<ChapterDraft>().mapNotNull { it.number }.maxOrNull() ?: 0) + 1
    add(ChapterDraft(id = newDraftId(), number = next, name = "", pages = emptyList()))
}

fun List<PageWithSearchItem>.duplicatedChapterNumbers(): Set<Int> =
    filterIsInstance<ChapterDraft>()
        .mapNotNull { it.number }
        .groupingBy { it }.eachCount()
        .filterValues { it > 1 }.keys

fun List<PageWithSearchItem>.chaptersAreValid(): Boolean =
    filterIsInstance<ChapterDraft>().all { it.number != null } && duplicatedChapterNumbers().isEmpty()

fun MutableList<PageWithSearchItem>.updatePage(id: Long, transform: (PageDraft) -> PageDraft) {
    val index = indexOfFirst { it.id == id }
    val old = getOrNull(index) as? PageDraft ?: return
    val new = transform(old)
    when {
        new.variants.isEmpty() -> removeAt(index)
        new != old -> this[index] = new
    }
}

fun MutableList<PageWithSearchItem>.setPageImage(pageId: Long, language: String, image: ImageData?) {
    updatePage(pageId) { page ->
        val exists = page.variants.any { it.language == language }
        page.copy(
            variants = when {
                image == null -> page.variants.filterNot { it.language == language }
                exists -> page.variants.map { if (it.language == language) it.copy(content = image) else it }
                else -> page.variants + PageVariantDraft(language, image)
            }
        )
    }
}

fun MutableList<PageWithSearchItem>.addPageImages(images: List<ImageData>, language: String) {
    val queue = images.toMutableList()
    for (i in indices) {
        if (queue.isEmpty()) break
        val page = this[i] as? PageDraft ?: continue
        if (page.variants.none { it.language == language }) {
            this[i] = page.copy(variants = page.variants + PageVariantDraft(language, queue.removeAt(0)))
        }
    }
    queue.forEach { add(PageDraft(id = newDraftId(), variants = listOf(PageVariantDraft(language, it)))) }
}

fun MutableList<PageWithSearchItem>.removeLanguage(language: String) {
    filterIsInstance<PageDraft>().map { it.id }.forEach { id ->
        updatePage(id) { page -> page.copy(variants = page.variants.filterNot { it.language == language }) }
    }
}

private val digitOrNot = Regex("""\d+|\D+""")

fun naturalCompare(a: String, b: String): Int {
    val pa = digitOrNot.findAll(a).map { it.value }.toList()
    val pb = digitOrNot.findAll(b).map { it.value }.toList()
    for (i in 0 until minOf(pa.size, pb.size)) {
        val x = pa[i]
        val y = pb[i]
        val c = if (x[0].isDigit() && y[0].isDigit()) {
            val tx = x.trimStart('0')
            val ty = y.trimStart('0')
            tx.length.compareTo(ty.length).takeIf { it != 0 } ?: tx.compareTo(ty)
        } else {
            x.compareTo(y, ignoreCase = true)
        }
        if (c != 0) return c
    }
    return pa.size.compareTo(pb.size)
}
