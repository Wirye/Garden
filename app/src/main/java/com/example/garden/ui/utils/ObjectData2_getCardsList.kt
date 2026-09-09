package com.example.garden.ui.utils

import com.example.garden.database.ElementType
import com.example.garden.database.ObjectData2

fun ObjectData2.getCardsList() : List<ObjectData2> {
    val res = mutableListOf<ObjectData2>()

    for (i in this.childs) {
        if (i.elementType == ElementType.PlaceholderCard) {
            res.add(i)
        }
    }

    return res
}