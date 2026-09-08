package com.example.garden.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberMultipleFilesPicker(
    mimeTypes: Array<String> = arrayOf("*/*"),
    onFilesSelected: (List<Uri>) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val currentOnResult by rememberUpdatedState(onFilesSelected)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        uris.forEach { uri ->
            try {
                context.contentResolver.takePersistableUriPermission(uri, flags)
            } catch (_: Exception) { }
        }

        currentOnResult(uris)
    }

    return {
        launcher.launch(mimeTypes)
    }
}