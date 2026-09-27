package com.example.garden.ui.utils

import com.example.garden.Layer
import com.example.garden.database.entities.CarouselType
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.ObjectData
import com.example.garden.database.entities.PlayListType

fun ObjectData.Card.toLayerCreateCardPage(parentId: Long, carouselType: CarouselType) : Layer.CreateCardPage {
    return Layer.CreateCardPage(
        name = this.name,
        carouselType = carouselType,
        parentId = parentId,
        cardId = this.id,
        cardPosition = this.position,
        description = when(this) {
            is ObjectData.Card.Anime -> this.description
            is ObjectData.Card.Manga -> this.description
            is ObjectData.Card.AniLibria -> this.data.description ?: ""
            is ObjectData.Card.Music -> ""
            is ObjectData.Card.Playlist -> ""
            is ObjectData.Card.Artist -> ""
            is ObjectData.Card.Album -> ""
        },
        authorsList = if (this !is ObjectData.Card.Music) listOf(this.author) else this.authors,
        image = this.image,
        cardType = when(this) {
            is ObjectData.Card.Anime, is ObjectData.Card.AniLibria -> ElementType.AnimeCard
            is ObjectData.Card.Manga -> ElementType.MangaCard
            is ObjectData.Card.Music -> ElementType.MusicCard
            is ObjectData.Card.Playlist -> ElementType.PlaylistCard
            is ObjectData.Card.Album -> ElementType.AlbumCard
            is ObjectData.Card.Artist -> ElementType.ArtistCard
        },
        genreList = this.genre,
        episodesList = if (this is ObjectData.Card.Anime) episodesList else emptyList(),
        chaptersList = if (this is ObjectData.Card.Manga) chaptersList else emptyList(),
        cardsList = if (this is ObjectData.Card.Playlist) cardsList else emptyList(),
        song = if (this is ObjectData.Card.Music) song else null,
        horizontalVideo = if (this is ObjectData.Card.Music) horizontalVideo else null,
        playlistType = if (this is ObjectData.Card.Playlist) playListType else PlayListType.Music
    )
}
