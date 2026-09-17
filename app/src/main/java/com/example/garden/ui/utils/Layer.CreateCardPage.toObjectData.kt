package com.example.garden.ui.utils

import com.example.garden.Layer
import com.example.garden.database.ElementType
import com.example.garden.database.ImageData
import com.example.garden.database.LinkData
import com.example.garden.database.ObjectData

fun Layer.CreateCardPage.toObjectData() : ObjectData {
    return when (cardType) {
        ElementType.AnimeCard -> {
            ObjectData.Card.Anime(
                id = cardId ?: 0L,
                position = cardPosition,
                name = name,
                author = author,
                description = description,
                genre = genreList,
                image = image ?: ImageData.Url(""),
                link = LinkData.Self
            )
        }

        ElementType.MangaCard -> {
            ObjectData.Card.Manga(
                id = cardId ?: 0L,
                name = name,
                author = author,
                description = description,
                genre = genreList,
                image = image ?: ImageData.Url(""),
                position = cardPosition,
                link = LinkData.Self
            )
        }

        ElementType.MusicCard -> {
            ObjectData.Card.Music(
                id = cardId ?: 0L,
                name = name,
                author = author,
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
                id = cardId ?: 0L,
                name = name,
                author = author,
                genre = genreList,
                image = image ?: ImageData.Url(""),
                position = cardPosition,
                link = LinkData.Self,
                cardsList = cardsList
            )
        }

        else -> {
            ObjectData.Card.Anime(
                id = cardId ?: 0L,
                name = name,
                author = author,
                description = description,
                genre = genreList,
                image = image ?: ImageData.Url(""),
                position = cardPosition,
                link = LinkData.Self
            )
        }
    }
}
