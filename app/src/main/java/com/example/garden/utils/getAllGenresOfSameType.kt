package com.example.garden.utils

import android.util.Log
import com.example.garden.database.GridGenreItem

fun getAllGenresOfSameType(selected: List<GridGenreItem>): List<GridGenreItem> {
    val result = mutableListOf<GridGenreItem>()
    val groupedByClass = selected.groupBy { it.getGenreClass() }
    for ((clazz, items) in groupedByClass) {
        if (clazz.isEnum) {
            @Suppress("UNCHECKED_CAST")
            val allEnumValues = (clazz.enumConstants as Array<GridGenreItem>).toList()
            result.addAll(allEnumValues)
        } else {
            result.addAll(items)
        }
    }
    return result
}