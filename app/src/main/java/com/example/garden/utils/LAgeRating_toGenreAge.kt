package com.example.garden.utils

import com.example.garden.database.entities.GenreAge
import com.github.wirye.anilibriakt.model.AgeRatings

fun AgeRatings.toGenreAge(): GenreAge? {
    return when(this) {
        AgeRatings.R0_PLUS -> GenreAge(age = 0)
        AgeRatings.R6_PLUS -> GenreAge(age = 6)
        AgeRatings.R12_PLUS -> GenreAge(age = 12)
        AgeRatings.R16_PLUS -> GenreAge(age = 16)
        AgeRatings.R18_PLUS -> GenreAge(age = 18)
    }
}
