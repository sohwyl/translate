package com.example.data

import kotlinx.coroutines.flow.Flow

class PhraseRepository(private val phraseDao: PhraseDao) {

    val allPhrases: Flow<List<PhraseEntity>> = phraseDao.getAllPhrases()
    val favoritePhrases: Flow<List<PhraseEntity>> = phraseDao.getFavoritePhrases()

    fun getPhrasesByCategory(category: String): Flow<List<PhraseEntity>> {
        return if (category == "همه" || category.startsWith("همه")) {
            phraseDao.getAllPhrases()
        } else {
            phraseDao.getPhrasesByCategory(category)
        }
    }

    fun searchPhrases(query: String): Flow<List<PhraseEntity>> {
        return phraseDao.searchPhrases(query)
    }

    suspend fun toggleFavorite(phrase: PhraseEntity) {
        phraseDao.updateFavoriteStatus(phrase.id, !phrase.isFavorite)
    }

    suspend fun ensureDatabasePopulated() {
        if (phraseDao.getCount() != 600) {
            phraseDao.deleteAll()
            phraseDao.insertAll(DatabaseInitializer.getInitialPhrases())
        }
    }
}
