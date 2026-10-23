package com.example.garden.utils

import com.example.garden.database.entities.Genre
import com.github.wirye.anilibriakt.model.TitleGenres

fun TitleGenres.toGenre(): Genre? {
    return when(this) {
        TitleGenres.ACTION -> Genre.Action
        TitleGenres.ADVENTURES -> Genre.Adventures
        TitleGenres.COMEDY -> Genre.Comedy
        TitleGenres.DRAMA -> Genre.Drama
        TitleGenres.FANTASY -> Genre.Fantasy
        TitleGenres.HORROR -> Genre.Horrors
        TitleGenres.MAGIC -> Genre.Magic
        TitleGenres.MYSTERY -> Genre.Mysticism
        TitleGenres.ROMANCE -> Genre.Romance
        TitleGenres.SHOUJO -> Genre.Shoujo
        TitleGenres.SHONEN -> Genre.Shonen
        TitleGenres.EVERYDAYLIFE -> Genre.EverydayLife
        TitleGenres.ETTY -> Genre.Etty
        TitleGenres.CYBERPUNK -> Genre.Cyberpunk
        TitleGenres.FANTASTIC -> Genre.Fantastic
        TitleGenres.GIRLSLOVE -> Genre.GirlsLove
        TitleGenres.SCHOOL -> Genre.School
        TitleGenres.TRILLER -> Genre.Thriller
        TitleGenres.DETECTIVE -> Genre.Detective
        TitleGenres.HAREM -> Genre.Harem
        TitleGenres.ISEKAI -> Genre.Isekai
        TitleGenres.MARTIALARTS -> Genre.MartialArts
        TitleGenres.DEMONS -> Genre.Demons
        TitleGenres.SUPERPOWER -> Genre.Superpower
        TitleGenres.SEINEN -> Genre.Seinen
        TitleGenres.PSYCHOLOGICAL -> Genre.Psychological
        TitleGenres.MYSTICISM -> Genre.Mysticism
        TitleGenres.MUSIC -> Genre.Music
        TitleGenres.GAMES -> Genre.Games
        else -> null
    }
}