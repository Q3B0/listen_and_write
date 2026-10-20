package com.codex.listenandwrite.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.codex.listenandwrite.data.entity.DictationItemEntity
import com.codex.listenandwrite.data.entity.DictationSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DictationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: DictationSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<DictationItemEntity>)

    @Query("SELECT * FROM dictation_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<DictationSessionEntity>>

    @Query("SELECT * FROM dictation_sessions WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getSessionsSince(sinceTimestamp: Long): Flow<List<DictationSessionEntity>>

    @Query("SELECT * FROM dictation_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): DictationSessionEntity?

    @Query("SELECT * FROM dictation_items WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    suspend fun getItemsForSession(sessionId: Long): List<DictationItemEntity>

    @Query("SELECT COUNT(*) FROM dictation_sessions")
    fun getTotalSessionsCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(totalCount), 0) FROM dictation_sessions")
    fun getTotalWordsCount(): Flow<Int>

    @Query("SELECT AVG(accuracy) FROM dictation_sessions")
    fun getAverageAccuracy(): Flow<Float?>
}
