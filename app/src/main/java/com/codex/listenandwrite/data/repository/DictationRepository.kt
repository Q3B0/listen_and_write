package com.codex.listenandwrite.data.repository

import com.codex.listenandwrite.data.dao.DictationDao
import com.codex.listenandwrite.data.entity.DictationItemEntity
import com.codex.listenandwrite.data.entity.DictationSessionEntity
import kotlinx.coroutines.flow.Flow

class DictationRepository(
    private val dictationDao: DictationDao
) {
    fun getAllSessions(): Flow<List<DictationSessionEntity>> = dictationDao.getAllSessions()

    fun getSessionsSince(sinceTimestamp: Long): Flow<List<DictationSessionEntity>> =
        dictationDao.getSessionsSince(sinceTimestamp)

    suspend fun getSessionById(sessionId: Long): DictationSessionEntity? =
        dictationDao.getSessionById(sessionId)

    suspend fun getItemsForSession(sessionId: Long): List<DictationItemEntity> =
        dictationDao.getItemsForSession(sessionId)

    fun getTotalSessionsCount(): Flow<Int> = dictationDao.getTotalSessionsCount()

    fun getTotalWordsCount(): Flow<Int> = dictationDao.getTotalWordsCount()

    fun getAverageAccuracy(): Flow<Float?> = dictationDao.getAverageAccuracy()

    suspend fun saveSession(
        session: DictationSessionEntity,
        items: List<DictationItemEntity>
    ): Long {
        val sessionId = dictationDao.insertSession(session)
        val itemsWithSessionId = items.map { it.copy(sessionId = sessionId) }
        dictationDao.insertItems(itemsWithSessionId)
        return sessionId
    }
}
