package com.codex.listenandwrite.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codex.listenandwrite.data.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE unitId = :unitId ORDER BY id ASC")
    fun getWordsForUnit(unitId: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE unitId IN (:unitIds)")
    suspend fun getWordsForUnits(unitIds: List<String>): List<WordEntity>

    @Query("SELECT * FROM words WHERE word LIKE '%' || :query || '%' OR translation LIKE '%' || :query || '%'")
    fun searchWords(query: String): Flow<List<WordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getTotalWordCount(): Int

    @Query("DELETE FROM words")
    suspend fun deleteAllWords()
}
