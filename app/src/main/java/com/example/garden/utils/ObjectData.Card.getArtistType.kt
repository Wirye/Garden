package com.example.garden.utils

import com.example.garden.database.entities.ArtistType
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.ObjectData

fun ObjectData.Card.getArtistType(): ArtistType? {
    return when (this) {
        is ObjectData.Card.Anime, is ObjectData.Card.AniLibria -> ArtistType.Anime
        is ObjectData.Card.Manga -> ArtistType.Manga
        is ObjectData.Card.Music -> ArtistType.Music
        is ObjectData.Card.Playlist -> null
        is ObjectData.Card.Artist -> null
        is ObjectData.Card.Album -> null
    }
}

fun ElementType.getArtistType(): ArtistType? {
    return when (this) {
        ElementType.AnimeCard -> ArtistType.Anime
        ElementType.MangaCard -> ArtistType.Manga
        ElementType.MusicCard -> ArtistType.Music
        else -> null
    }
}