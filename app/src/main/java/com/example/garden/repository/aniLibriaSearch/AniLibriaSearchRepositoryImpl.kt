package com.example.garden.repository.aniLibriaSearch

import com.example.garden.appsettings.AuthManager
import com.github.wirye.anilibriakt.AniLibriaClient
import com.github.wirye.anilibriakt.model.SearchOffer
import com.github.wirye.anilibriakt.model.Title

class AniLibriaSearchRepositoryImpl(private val authManager: AuthManager) : AniLibriaSearchRepository {
    private val anilibriaApi = AniLibriaClient(tokenProvider = { authManager.getToken() })

    override suspend fun getOffers(query: String, limit: Int): Result<List<SearchOffer>> = anilibriaApi.search.getOffers(query, limit)

    override suspend fun search(query: String, limit: Int): Result<List<Title>> = anilibriaApi.search.search(query, limit)
}
