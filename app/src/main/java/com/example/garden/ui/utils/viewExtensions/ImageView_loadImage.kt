package com.example.garden.ui.utils.viewExtensions

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.widget.ImageView
import coil.load
import com.example.garden.database.ImageData
import com.example.garden.database.ImageSource
import java.io.File
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import com.example.garden.R

val Context.lifecycleOwner: LifecycleOwner?
    get() {
        var curContext = this
        while (curContext is ContextWrapper) {
            if (curContext is LifecycleOwner) return curContext
            curContext = curContext.baseContext
        }
        return curContext as? LifecycleOwner
    }
fun ImageView.loadImage(image: ImageData?) {
    if (image == null) {
        this.load(R.drawable.placeholder)
        return
    }

    when (image.source) {
        ImageSource.URL -> {
            this.load(image.value) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
        }
        ImageSource.DEVICE -> {
            this.load(if (image.value.startsWith("content://")) image.value.toUri() else File(image.value)) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
                listener(onError = { _, result ->
                    Log.e("CoilError", "Ошибка загрузки из памяти: ${result.throwable}")
                })
            }
        }
        ImageSource.SELF -> {
            val imageId = image.value.toIntOrNull() ?: R.drawable.placeholder
            this.load(imageId) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
        }
    }
}