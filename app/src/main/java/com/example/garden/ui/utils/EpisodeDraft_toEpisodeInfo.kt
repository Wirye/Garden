package com.example.garden.ui.utils

import com.example.garden.database.entities.EpisodeInfo
import com.example.garden.database.entities.EpisodeVariant
import com.example.garden.database.entities.ImageData
import com.example.garden.ui.components.EpisodeDraft

fun EpisodeDraft.toEpisodeInfo() = EpisodeInfo(
    position = requireNotNull(number) { "validate before saving" },
    name = name,
    preview = image ?: ImageData.Device(""),
    variants = variants.map { EpisodeVariant(voice = it.voice, link = it.link) }
)