package com.example.garden.utils

fun String.isWebId(): Boolean {
    return (this.contains("Web") || this.contains("web") || this.contains("WEB"))
}
