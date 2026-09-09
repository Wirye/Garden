package com.example.garden.ui.utils

import com.example.garden.database.ElementType
import com.example.garden.database.LinkData
import com.example.garden.database.ObjectData2

fun ObjectData2.getHorizontalVideo() : LinkData? {
    var res: LinkData? = null

    for (i in this.childs) {
        if (i.elementType == ElementType.SongHorizontalVideo) {
            val link = i.link
            if (link != null) {
                res = link
            }
        }
    }

    return res
}