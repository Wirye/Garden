package com.example.garden.utils

import com.example.garden.database.entities.Genre
import com.example.garden.database.entities.GridGenreItem
import com.example.garden.database.entities.MusicGenre

fun getAllGenresOfSameType(selected: List<GridGenreItem>): List<GridGenreItem> {
    return selected.flatMap { item ->
        when (item) {
            is Genre -> Genre.entries
            is MusicGenre -> MusicGenre.entries
            else -> listOf(item)
        }
    }.distinct()
}
