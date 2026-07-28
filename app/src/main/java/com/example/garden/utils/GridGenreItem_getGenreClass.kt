package com.example.garden.utils

import com.example.garden.database.GridGenreItem

fun GridGenreItem.getGenreClass(): Class<*> = this::class.java.superclass?.takeIf { it.isEnum } ?: this::class.java