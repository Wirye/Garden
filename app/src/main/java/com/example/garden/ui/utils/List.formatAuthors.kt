package com.example.garden.ui.utils

fun List<String>.formatAuthors(): String = when (size) {
    0 -> ""
    1 -> first()
    else -> dropLast(1).joinToString(", ") + " & " + last()
}
