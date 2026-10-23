package com.example.garden.ui.utils

import com.example.garden.database.entities.ChapterInfo
import com.example.garden.ui.components.newDraftId
import com.example.garden.ui.screens.ChapterDraft
import com.example.garden.ui.screens.PageDraft
import com.example.garden.ui.screens.PageVariantDraft

fun ChapterInfo.toDraft() = ChapterDraft(
    id = newDraftId(),
    number = position,
    name = name,
    pages = pages.sortedBy { it.position }.map { page ->
        PageDraft(
            id = newDraftId(),
            variants = page.variants.map { PageVariantDraft(language = it.language, content = it.content) }
        )
    }
)
