package com.example.garden.utils.search

fun buildFuzzyFtsQuery(input: String): String {
    val clean = input.trim().lowercase()
    if (clean.isEmpty()) return ""

    val words = clean.split("\\s+".toRegex()).filter { it.isNotBlank() }
    if (words.isEmpty()) return ""

    val wordsQuery = words.joinToString(separator = " AND ") { word ->
        buildSingleWordFts(word)
    }

    return if (words.size > 1) {
        val mergedWord = words.joinToString(separator = "")
        val mergedQuery = buildSingleWordFts(mergedWord)

        "($wordsQuery) OR ($mergedQuery)"
    } else {
        wordsQuery
    }
}

private fun expandLetterVariants(text: String): List<String> {
    var results = listOf("")
    for (char in text) {
        val options = when (char) {
            'е', 'ё' -> listOf("е", "ё")
            'и', 'й' -> listOf("и", "й")
            else -> listOf(char.toString())
        }
        results = results.flatMap { prefix -> options.map { prefix + it } }
    }
    return results.distinct()
}

private fun buildSingleWordFts(word: String): String {
    if (word.length <= 3) {
        val variants = expandLetterVariants(word)
        return "(${variants.joinToString(separator = " OR ") { "$it*" }})"
    } else {
        val wordVariants = expandLetterVariants(word).map { "$it*" }
        val trigramVariants = word.windowed(size = 3, step = 1)
            .take(4)
            .flatMap { expandLetterVariants(it) }
            .map { "$it*" }

        val allClauses = (wordVariants + trigramVariants).distinct()
        return "(${allClauses.joinToString(separator = " OR ")})"
    }
}
