package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PhraseDao {
    @Query("SELECT * FROM phrases ORDER BY id ASC")
    fun getAllPhrases(): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE category = :category ORDER BY id ASC")
    fun getPhrasesByCategory(category: String): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE isFavorite = 1 ORDER BY id ASC")
    fun getFavoritePhrases(): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE arabic LIKE '%' || :query || '%' OR arabic_readable LIKE '%' || :query || '%' OR persian LIKE '%' || :query || '%' OR finglish LIKE '%' || :query || '%'")
    fun searchPhrases(query: String): Flow<List<PhraseEntity>>

    @Query("DELETE FROM phrases")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM phrases")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(phrases: List<PhraseEntity>)

    @Update
    suspend fun updatePhrase(phrase: PhraseEntity)

    @Query("UPDATE phrases SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)
}
