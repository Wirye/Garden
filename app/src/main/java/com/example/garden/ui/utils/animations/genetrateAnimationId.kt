package com.example.garden.ui.utils.animations

private var lastAnimationId = 0L

fun generateAnimationId(): Long {
    val res = lastAnimationId
    lastAnimationId += 1L
    return res
}