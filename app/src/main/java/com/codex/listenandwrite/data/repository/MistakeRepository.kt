package com.codex.listenandwrite.data.repository

import com.codex.listenandwrite.data.dao.MistakeDao
import com.codex.listenandwrite.data.entity.MistakeRecordEntity
import kotlinx.coroutines.flow.Flow

class MistakeRepository(
    private val mistakeDao: MistakeDao
) {
    fun getAllMistakes(): Flow<List<MistakeRecordEntity>> = mistakeDao.getAllMistakes()

    fun getUnmasteredMistakes(): Flow<List<MistakeRecordEntity>> = mistakeDao.getUnmasteredMistakes()

    fun getMistakesByUnit(): Flow<List<MistakeRecordEntity>> = mistakeDao.getMistakesByUnit()

    fun getUnmasteredCount(): Flow<Int> = mistakeDao.getUnmasteredCount()

    fun getTotalMistakesCount(): Flow<Int> = mistakeDao.getTotalMistakesCount()

    suspend fun recordMistake(
        word: String,
        phonetic: String,
        translation: String,
        unitTitle: String
    ) {
        val existing = mistakeDao.getMistakeByWord(word)
        if (existing != null) {
            val updated = existing.copy(
                mistakeCount = existing.mistakeCount + 1,
                lastMistakeTime = System.currentTimeMillis(),
                isMastered = false
            )
            mistakeDao.update(updated)
        } else {
            val newRecord = MistakeRecordEntity(
                word = word,
                phonetic = phonetic,
                translation = translation,
                unitTitle = unitTitle,
                mistakeCount = 1,
                lastMistakeTime = System.currentTimeMillis(),
                isMastered = false
            )
            mistakeDao.insertOrUpdate(newRecord)
        }
    }

    suspend fun setMasteredStatus(id: Long, isMastered: Boolean) {
        mistakeDao.setMasteredStatus(id, isMastered)
    }

    suspend fun setFavoriteStatus(id: Long, isFavorite: Boolean) {
        mistakeDao.setFavoriteStatus(id, isFavorite)
    }

    suspend fun deleteMistake(id: Long) {
        mistakeDao.deleteById(id)
    }
}
