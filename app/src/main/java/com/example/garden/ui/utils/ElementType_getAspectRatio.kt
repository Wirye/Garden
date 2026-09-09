package com.example.garden.ui.utils

import com.example.garden.database.ElementType

fun ElementType.getAspectRatio(): Float {
    return when (this) {
        ElementType.AnimeCard, ElementType.MangaCard -> (2f / 3f)
        ElementType.MusicCard, ElementType.PlaylistCard, ElementType.Chapter, ElementType.Song,
        ElementType.Carousel, ElementType.ChapterPage, ElementType.PlaceholderCard -> (1f / 1f)
        ElementType.Episode, ElementType.SongHorizontalVideo -> (16f / 9f)
        ElementType.SongVerticalVideo -> (9f / 16f)
    }
}