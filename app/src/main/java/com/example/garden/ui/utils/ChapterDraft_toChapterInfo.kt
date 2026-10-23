package com.example.garden.ui.utils

import com.example.garden.database.entities.ChapterInfo
import com.example.garden.database.entities.ChapterPageInfo
import com.example.garden.database.entities.ChapterPageVariant
import com.example.garden.ui.screens.ChapterDraft

fun ChapterDraft.toChapterInfo() = ChapterInfo(
    position = requireNotNull(number) { "validate before saving" },
    name = name,
    pages = pages.mapIndexed { index, page ->
        ChapterPageInfo(
            position = index,
            variants = page.variants.map { ChapterPageVariant(language = it.language, content = it.content) }
        )
    }
)