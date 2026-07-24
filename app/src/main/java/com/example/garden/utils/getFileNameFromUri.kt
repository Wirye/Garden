package com.example.garden.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun getFileNameFromUri(uri: Uri, context: Context): String? {
    if (uri.scheme == "file") {
        return uri.lastPathSegment
    }
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                return cursor.getString(nameIndex)
            }
        }
    }
    return uri.lastPathSegment
}