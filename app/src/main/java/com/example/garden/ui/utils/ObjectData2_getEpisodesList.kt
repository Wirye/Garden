package com.example.garden.ui.utils

import com.example.garden.database.ElementType
import com.example.garden.database.LinkData
import com.example.garden.database.LinkType
import com.example.garden.database.ObjectData2
import com.example.garden.ui.screens.EpisodeInfo

fun ObjectData2.getEpisodesList() : List<EpisodeInfo> {
    val res = mutableListOf<EpisodeInfo>()
    for (i in this.childs) {
        if (i.elementType == ElementType.Episode) {
            res.add(
                EpisodeInfo(
                    name = i.name ?: "",
                    image = i.image,
                    length = i.length,
                    link = i.link ?: LinkData(LinkType.CONTENT, null, null),
                    id = i.id
                )
            )
        }
    }
    return res
}
