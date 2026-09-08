package com.example.garden.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.floor
import androidx.compose.runtime.State

@Composable
fun rememberMediaDuration(uriString: String?): State<Long> {
    val context = LocalContext.current

    return produceState(initialValue = 0L, key1 = uriString) {
        if (uriString.isNullOrEmpty()) {
            value = 0L
            return@produceState
        }

        value = withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uriString.toUri())
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                floor((time?.toLong() ?: 0L) / 1000f).toLong()
            } catch (e: Exception) {
                Log.e("getMediaDuration", "Error extracting duration: ${e.message}")
                0L
            } finally {
                try {
                    retriever.release()
                } catch (_: Exception) { }
            }
        }
    }
}

suspend fun Context.getMediaDuration(uriString: String): Long = withContext(Dispatchers.IO) {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(this@getMediaDuration, uriString.toUri())
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        time?.toLong() ?: 0L
    } catch (e: Exception) {
        Log.e("getMediaDuration", "Error extracting duration: ${e.message}")
        0L
    } finally {
        try {
            retriever.release()
        } catch (_: Exception) { }
    }
}