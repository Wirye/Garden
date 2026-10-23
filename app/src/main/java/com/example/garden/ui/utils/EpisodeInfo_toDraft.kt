package com.example.garden.ui.utils

import com.example.garden.database.entities.EpisodeInfo
import com.example.garden.ui.components.EpisodeDraft
import com.example.garden.ui.components.VariantDraft
import com.example.garden.ui.components.newDraftId

fun EpisodeInfo.toDraft() = EpisodeDraft(
    id = newDraftId(),
    number = position,
    name = name,
    image = preview,
    variants = variants.map { VariantDraft(newDraftId(), it.voice, it.link) }
)