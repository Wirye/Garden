package com.example.garden.utils

fun String.toLongId(): Long {
    var id = -1L
    try {
        val s = this.split("_")
        id = s[1].toLong()
    } catch (_: Exception) { }
    if (id == -1L) {
        try {
            id = this.toLong()
        } catch (_: Exception) { }
    }
    return id
}