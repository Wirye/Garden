package com.example.garden.ui.utils

import com.example.garden.database.entities.EpisodeInfo
import com.example.garden.database.entities.EpisodeVariant
import com.example.garden.database.entities.ImageData
import com.example.garden.database.entities.LinkData
import com.example.garden.database.entities.VoiceInfo
import com.github.wirye.anilibriakt.model.Episode

fun Episode.toEpisodeInfo(
    position: Int,
    localEpisodes: List<EpisodeInfo>,
    watched: Boolean = false,
    time: Float? = null
): EpisodeInfo {
    val localEpWithThisPosition = localEpisodes.firstOrNull { it.position == position }
    val duration = this.duration
    return localEpWithThisPosition?.copy(
        name = localEpWithThisPosition.name.ifBlank { russianName ?: "" },
        positionPr = if (localEpWithThisPosition.positionPr > 0f) localEpWithThisPosition.positionPr else {
            if (time != null && duration != null && duration > 0L) {
                time.coerceAtMost(duration.toFloat()) / duration.toFloat()
            } else 0f
        },
        watched = if (localEpWithThisPosition.positionPr > 0f) localEpWithThisPosition.watched else watched,
        preview = if (!localEpWithThisPosition.preview.isNull()) localEpWithThisPosition.preview else ImageData.Url(
            (this.preview?.fullPreviewUrl) ?: ""
        ),
        variants = localEpWithThisPosition.variants.toMutableList().apply {
            if (this.none { it.voice.isAniLibria }) {
                add(
                    EpisodeVariant(
                        voice = VoiceInfo(
                            name = "AniLibria",
                            isAniLibria = true
                        ),
                        link = LinkData.AniLibriaEpisode(episodeId)
                    )
                )
            }
        }.toList()
    )
        ?: EpisodeInfo(
            position = position,
            name = this.russianName ?: "",
            positionPr =
                if (time != null && duration != null && duration > 0L) {
                    time.coerceAtMost(duration.toFloat()) / duration.toFloat()
                } else 0f,
            preview = ImageData.Url((this.preview?.fullPreviewUrl) ?: ""),
            watched = watched,
            variants = listOf(
                EpisodeVariant(
                    voice = VoiceInfo(
                        name = "AniLibria",
                        isAniLibria = true
                    ),
                    link = LinkData.AniLibriaEpisode(this.episodeId)
                )
            )
        )
}
