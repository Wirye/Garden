package com.example.garden.utils

import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.ObjectData

fun ObjectData.Card.getElementType() : ElementType {
    return when (this) {
        is ObjectData.Card.Anime, is ObjectData.Card.AniLibria -> ElementType.AnimeCard
        is ObjectData.Card.Manga -> ElementType.MangaCard
        is ObjectData.Card.Music -> ElementType.MusicCard
        is ObjectData.Card.Playlist -> ElementType.PlaylistCard
        is ObjectData.Card.Album -> ElementType.AlbumCard
        is ObjectData.Card.Artist -> ElementType.ArtistCard
    }
}