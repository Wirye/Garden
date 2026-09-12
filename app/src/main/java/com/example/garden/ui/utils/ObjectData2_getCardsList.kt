package com.example.garden.ui.utils

import com.example.garden.database.ElementType
import com.example.garden.database.ObjectData2

fun ObjectData2.getCardsList() : List<Long> {
    val res = mutableListOf<Long>()

    for (i in this.childs) {
        if (i.elementType == ElementType.PlaceholderCard) {
            res.add(i.id)
        }
    }

    return res
}