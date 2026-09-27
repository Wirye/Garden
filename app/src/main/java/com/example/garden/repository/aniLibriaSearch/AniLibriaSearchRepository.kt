package com.example.garden.repository.aniLibriaSearch

import com.github.wirye.anilibriakt.model.SearchOffer
import com.github.wirye.anilibriakt.model.Title

interface AniLibriaSearchRepository {
    suspend fun getOffers(query: String, limit: Int): Result<List<SearchOffer>>
    suspend fun search(query: String, limit: Int): Result<List<Title>>
}