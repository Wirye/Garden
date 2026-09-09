package com.example.garden.ui.utils

import com.example.garden.database.ElementType
import com.example.garden.database.ImageData
import com.example.garden.database.LinkData
import com.example.garden.database.LinkType
import com.example.garden.database.ObjectData2
import com.example.garden.ui.screens.ChapterInfo
import com.example.garden.ui.screens.ChapterPageInfo
import com.example.garden.ui.screens.PageWithSearchItem

fun ObjectData2.getChaptersList() : List<ChapterInfo> {
    val res = mutableListOf<ChapterInfo>()
    for (i in this.childs) {
        if (i.elementType == ElementType.Chapter) {
            val childs = mutableListOf<PageWithSearchItem>()
            for (o in i.childs) {
                if (o.elementType == ElementType.ChapterPage) {
                    childs.add(
                        ChapterPageInfo(
                            id = o.id,
                            image = o.image ?: ImageData.Device(""),
                        )
                    )
                }
            }

            res.add(
                ChapterInfo(
                    id = i.id,
                    name = i.name ?: "",
                    childs = childs,
                    link = i.link ?: LinkData(LinkType.SELF, null, null)
                )
            )
        }
    }
    return res
}
