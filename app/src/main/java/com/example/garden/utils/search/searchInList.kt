package com.example.garden.utils.search

import me.xdrop.fuzzywuzzy.FuzzySearch

data class SearchResult(
    val text: String,
    val score: Int
)
fun searchInList(list: List<String>, text: String, minScore: Int): List<String> {
    if (text.isEmpty()) return emptyList()
    val minScore = minScore.coerceIn(0,100)
    val cleanQuery = text.trim().lowercase()
    if (cleanQuery.isEmpty()) return list
    return list.mapNotNull { item ->
        val itemText = item.lowercase()
        val isPrefixMatch = itemText.split(" ").any { word -> word.startsWith(cleanQuery) }
        if (isPrefixMatch) {
            SearchResult(item, 100)
        } else {
            val score = FuzzySearch.extractOne(cleanQuery, listOf(itemText)).score
            if (score >= minScore) SearchResult(item, score) else null
        }
    }.sortedByDescending { it.score }.map { it.text }
}