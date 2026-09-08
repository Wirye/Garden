package com.example.garden.ui.components

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberFilePicker(
    mimeTypes: Array<String> = arrayOf("*/*"),
    onFileSelected: (Uri?) -> Unit
): () -> Unit {
    val currentOnResult by rememberUpdatedState(onFileSelected)

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val takeFlags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

            try {
                context.contentResolver.takePersistableUriPermission(it, takeFlags)

                currentOnResult(it)
            } catch (e: Exception) { }
        }
    }

    return {
        launcher.launch(mimeTypes)
    }
}