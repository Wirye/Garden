package com.example.garden.ui.utils

import com.example.garden.database.entities.CarouselType
import com.example.garden.database.entities.ElementType

fun CarouselType.availableCardTypes(): List<ElementType> {
    return when (this) {
        CarouselType.Anime -> listOf(ElementType.AnimeCard)
        CarouselType.Manga -> listOf(ElementType.MangaCard)
        CarouselType.Music -> listOf(ElementType.MusicCard)
        CarouselType.Playlist -> listOf(ElementType.PlaylistCard)
        CarouselType.Album -> listOf(ElementType.AlbumCard)
        CarouselType.Artist -> listOf(ElementType.ArtistCard)
        CarouselType.ArtistNAlbum -> listOf(ElementType.ArtistCard, ElementType.AlbumCard)
        CarouselType.ArtistNMusic -> listOf(ElementType.ArtistCard, ElementType.MusicCard)
        CarouselType.ArtistNMusicNAlbum -> listOf(ElementType.ArtistCard, ElementType.MusicCard, ElementType.AlbumCard)
        CarouselType.AlbumNMusic -> listOf(ElementType.AlbumCard, ElementType.MusicCard)
        CarouselType.AnimeNManga -> listOf(ElementType.AnimeCard, ElementType.MangaCard)
        CarouselType.PlaylistNMusic -> listOf(ElementType.PlaylistCard, ElementType.MusicCard)
        CarouselType.All -> ElementType.entries.toList()
    }
}