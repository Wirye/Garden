package com.example.garden.ui.utils

import com.example.garden.database.ImageData

fun ImageData.dataForModel() : Any {
    return when(this) {
        is ImageData.Device -> this.path
        is ImageData.Url -> this.url
        is ImageData.Resource -> this.ico.imageVector
    }
}