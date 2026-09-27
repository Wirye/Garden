package com.example.garden.utils

fun String.isLocalId(): Boolean {
    return (this.contains("Local") || this.contains("local") || this.contains("LOCAL"))
}
