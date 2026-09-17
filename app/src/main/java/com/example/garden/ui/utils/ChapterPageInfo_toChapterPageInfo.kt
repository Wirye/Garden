package com.example.garden.ui.utils

import com.example.garden.database.ChapterPageInfo
import com.example.garden.ui.screens.generateNewChapterId

fun ChapterPageInfo.toChapterPageInfo() : com.example.garden.ui.screens.ChapterPageInfo {
    return com.example.garden.ui.screens.ChapterPageInfo(
        id = generateNewChapterId(),
        image = content,
    )
}

fun com.example.garden.ui.screens.ChapterPageInfo.toChapterPageInfo(position: Int) : ChapterPageInfo {
    return ChapterPageInfo(
        position = position,
        content = image
    )
}