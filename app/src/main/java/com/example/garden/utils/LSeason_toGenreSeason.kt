package com.example.garden.utils

import com.example.garden.database.entities.GenreSezon
import com.github.wirye.anilibriakt.model.YearSezon

fun YearSezon.toGenreSeason(): GenreSezon {
    return when(this) {
        YearSezon.WINTER -> GenreSezon(sezon = com.example.garden.database.entities.YearSezon.WINTER)
        YearSezon.SUMMER -> GenreSezon(sezon = com.example.garden.database.entities.YearSezon.SUMMER)
        YearSezon.SPRING -> GenreSezon(sezon = com.example.garden.database.entities.YearSezon.SPRING)
        YearSezon.AUTUMN -> GenreSezon(sezon = com.example.garden.database.entities.YearSezon.AUTUMN)
    }
}