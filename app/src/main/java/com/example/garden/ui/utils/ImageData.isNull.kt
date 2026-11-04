package com.example.garden.ui.utils

import com.example.garden.database.entities.ImageData

fun ImageData.isNull() : Boolean {
    return when (this) {
        is ImageData.Device -> {
            path == ""
        }

        is ImageData.Url -> {
            url == ""
        }

        else -> {
            true
        }
    }
}