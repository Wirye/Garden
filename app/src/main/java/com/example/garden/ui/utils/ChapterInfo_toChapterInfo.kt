package com.example.garden.ui.utils

import com.example.garden.database.ChapterInfo
import com.example.garden.database.ChapterPageInfo
import com.example.garden.database.LinkData
import com.example.garden.ui.screens.generateNewChapterId

fun ChapterInfo.toChapterInfo() : com.example.garden.ui.screens.ChapterInfo {
    return com.example.garden.ui.screens.ChapterInfo(
        id = generateNewChapterId(),
        name = name,
        link = LinkData.Self,
        childs = pages.map { it.toChapterPageInfo() }.toMutableList()
    )
}

@Suppress("UNCHECKED_CAST")
fun com.example.garden.ui.screens.ChapterInfo.toChapterInfo(position: Int) : ChapterInfo {
    val pages = childs.filter { it::class.java == ChapterPageInfo::class.java } as List<com.example.garden.ui.screens.ChapterPageInfo>

    return ChapterInfo(
        name = name,
        position = position,
        pages = pages.map { it.toChapterPageInfo(pages.indexOf(it)) }
    )
}
