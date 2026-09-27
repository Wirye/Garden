package com.example.garden.ui.utils

import com.example.garden.database.entities.EpisodeInfo
import com.example.garden.database.entities.ImageData
import com.example.garden.ui.screens.generateNewChapterId

fun EpisodeInfo.toEpisodeInfo() : com.example.garden.ui.screens.EpisodeInfo {
    return com.example.garden.ui.screens.EpisodeInfo(
        id = generateNewChapterId(),
        image = preview,
        name = name,
        link = link,
        length = 0L,
    )
}

fun com.example.garden.ui.screens.EpisodeInfo.toEpisodeInfo(position: Int) : EpisodeInfo {
    return EpisodeInfo(
        preview = image ?: ImageData.Url(""),
        name = name,
        link = link,
        position = position
    )
}