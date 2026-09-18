package com.example.garden.ui.utils

import com.example.garden.database.ElementType
import com.example.garden.database.PlayListType

fun PlayListType.availableCardTypes(): List<ElementType> {
    return when(this) {
        PlayListType.Anime -> listOf(ElementType.AnimeCard)
        PlayListType.Manga -> listOf(ElementType.MangaCard)
        PlayListType.Music -> listOf(ElementType.MusicCard)
        PlayListType.AnimeNManga -> listOf(ElementType.AnimeCard, ElementType.MangaCard)
    }
}
