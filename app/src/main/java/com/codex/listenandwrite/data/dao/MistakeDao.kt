package com.codex.listenandwrite.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.codex.listenandwrite.data.entity.MistakeRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeDao {
    @Query("SELECT * FROM mistake_records ORDER BY lastMistakeTime DESC")
    fun getAllMistakes(): Flow<List<MistakeRecordEntity>>

    @Query("SELECT * FROM mistake_records WHERE isMastered = 0 ORDER BY lastMistakeTime DESC")
    fun getUnmasteredMistakes(): Flow<List<MistakeRecordEntity>>

    @Query("SELECT * FROM mistake_records ORDER BY unitTitle ASC, lastMistakeTime DESC")
    fun getMistakesByUnit(): Flow<List<MistakeRecordEntity>>

    @Query("SELECT * FROM mistake_records WHERE word = :word LIMIT 1")
    suspend fun getMistakeByWord(word: String): MistakeRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: MistakeRecordEntity)

    @Update
    suspend fun update(record: MistakeRecordEntity)

    @Query("UPDATE mistake_records SET isMastered = :isMastered WHERE id = :id")
    suspend fun setMasteredStatus(id: Long, isMastered: Boolean)

    @Query("UPDATE mistake_records SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM mistake_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM mistake_records WHERE isMastered = 0")
    fun getUnmasteredCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM mistake_records")
    fun getTotalMistakesCount(): Flow<Int>

    @Query("DELETE FROM mistake_records")
    suspend fun deleteAllMistakes()
}
