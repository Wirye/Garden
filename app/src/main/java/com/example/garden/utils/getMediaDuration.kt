package com.example.garden.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.core.net.toUri
import kotlin.math.floor

fun getMediaDuration(uriString: String, context: Context): Long {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, uriString.toUri())
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        return floor((time?.toLong() ?: 0L) / 1000f).toLong()
    } catch (e: Exception) {
        Log.e("getVideoDuration", "Something is incorrect: ${e.message}")
        return 0L
    } finally {
        retriever.release()
    }
}