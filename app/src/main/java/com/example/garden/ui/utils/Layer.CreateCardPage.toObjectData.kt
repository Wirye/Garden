package com.example.garden.ui.utils

import com.example.garden.Layer
import com.example.garden.database.ElementType
import com.example.garden.database.LinkData
import com.example.garden.database.LinkType
import com.example.garden.database.ObjectData
import com.example.garden.database.PageType

fun Layer.CreateCardPage.toObjectData(songLength: Long? = null) : ObjectData {

    return ObjectData(
        id = cardId ?: 0L,
        parentId = parentId,
        name = name,
        author = author,
        description = description,
        genre = genreList,
        elementType = cardType,
        image = image,
        alreadyWatched = 0L,
        length = if (cardType == ElementType.AnimeCard) {
            episodesList.sumOf { it.length }
        } else if (cardType == ElementType.MangaCard) {
            chaptersList.size.toLong()
        } else if (cardType == ElementType.MusicCard && song != null && songLength != null) {
            songLength.coerceAtLeast(1L)
        } else 1L,
        position = 0,
        page = PageType.Home,
        link = LinkData(
            type = LinkType.SELF,
            targetId = null,
            contentPath = null
        )
    )
}