package com.example.garden.ui.utils

import com.example.garden.Layer
import com.example.garden.database.entities.ArtistType
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.EntitySourceType
import com.example.garden.database.entities.ImageData
import com.example.garden.database.entities.LinkData
import com.example.garden.database.entities.ObjectData

fun Layer.CreateCardPage.toObjectData() : ObjectData {
    return when (cardType) {
        ElementType.AnimeCard -> {
            ObjectData.Card.Anime(
                id = cardId ?: "${EntitySourceType.Local.name}_0",
                position = cardPosition,
                name = name,
                author = authorsList.firstOrNull() ?: "",
                description = description,
                genre = genreList,
                image = image ?: ImageData.Url(""),
                link = LinkData.Self
            )
        }

        ElementType.MangaCard -> {
            ObjectData.Card.Manga(
                id = cardId ?: "${EntitySourceType.Local.name}_0",
                name = name,
                author = authorsList.firstOrNull() ?: "",
                description = description,
                genre = genreList,
                image = image ?: ImageData.Url(""),
                position = cardPosition,
                link = LinkData.Self
            )
        }

        ElementType.MusicCard -> {
            ObjectData.Card.Music(
                id = cardId ?: "${EntitySourceType.Local.name}_0",
                name = name,
                author = "",
                authors = authorsList,
                genre = genreList,
                image = image ?: ImageData.Url(""),
                position = cardPosition,
                link = LinkData.Self,
                song = song,
                horizontalVideo = horizontalVideo
            )
        }

        ElementType.PlaylistCard -> {
            ObjectData.Card.Playlist(
                id = cardId ?: "${EntitySourceType.Local.name}_0",
                name = name,
                author = authorsList.firstOrNull() ?: "",
                genre = genreList,
                image = image ?: ImageData.Url(""),
                position = cardPosition,
                link = LinkData.Self,
                cardsList = cardsList,
                playListType = playlistType
            )
        }

        ElementType.AlbumCard -> {
            ObjectData.Card.Album(
                id = cardId ?: "${EntitySourceType.Local.name}_0",
                name = name,
                author = authorsList.firstOrNull() ?: "",
                genre = genreList,
                image = image ?: ImageData.Url(""),
                position = cardPosition,
                link = LinkData.Self,
                songs = cardsList
            )
        }

        ElementType.ArtistCard -> {
            ObjectData.Card.Artist(
                id = cardId ?: "${EntitySourceType.Local.name}_0",
                name = name,
                author = authorsList.firstOrNull() ?: "",
                genre = genreList,
                image = image ?: ImageData.Url(""),
                position = cardPosition,
                link = LinkData.Self,
                artistType = artistType ?: ArtistType.Music
            )
        }

        ElementType.Carousel -> {
            ObjectData.Card.Anime(
                id = cardId ?: "${EntitySourceType.Local.name}_0",
                position = cardPosition,
                name = name,
                author = authorsList.firstOrNull() ?: "",
                description = description,
                genre = genreList,
                image = image ?: ImageData.Url(""),
                link = LinkData.Self
            )
        }
    }
}
