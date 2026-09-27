package com.example.garden.ui.utils

import com.example.garden.database.entities.ElementType

fun ElementType.getAspectRatio(): Float {
    return when (this) {
        ElementType.AnimeCard, ElementType.MangaCard -> (2f / 3f)
        ElementType.MusicCard, ElementType.PlaylistCard, ElementType.ArtistCard,
        ElementType.AlbumCard, ElementType.Carousel -> (1f / 1f)
    }
}