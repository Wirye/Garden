package com.example.garden.utils
private var lastLayerId = 0L
fun getValidLayerId() : Long {
    val q = lastLayerId
    lastLayerId += 1
    return q
}