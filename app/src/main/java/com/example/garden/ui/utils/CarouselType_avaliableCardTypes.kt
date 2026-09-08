package com.example.garden.ui.utils

import com.example.garden.database.CarouselType
import com.example.garden.database.ElementType

fun CarouselType.availableCardTypes(): List<ElementType> {
    return when (this) {
        CarouselType.Anime -> listOf(ElementType.AnimeCard)
        CarouselType.Manga -> listOf(ElementType.MangaCard)
        CarouselType.Music -> listOf(ElementType.MusicCard)
        CarouselType.Playlist -> listOf(ElementType.PlaylistCard)
        CarouselType.AnimeNManga -> listOf(ElementType.AnimeCard, ElementType.MangaCard)
        CarouselType.PlaylistNMusic -> listOf(ElementType.PlaylistCard, ElementType.MusicCard)
    }
}