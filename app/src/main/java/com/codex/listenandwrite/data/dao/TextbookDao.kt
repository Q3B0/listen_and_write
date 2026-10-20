package com.codex.listenandwrite.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.codex.listenandwrite.data.entity.TextbookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TextbookDao {
    @Query("SELECT * FROM textbooks ORDER BY grade ASC, semester ASC")
    fun getAllTextbooks(): Flow<List<TextbookEntity>>

    @Query("SELECT * FROM textbooks WHERE id = :id")
    suspend fun getTextbookById(id: String): TextbookEntity?

    @Query("SELECT * FROM textbooks WHERE isSelected = 1 LIMIT 1")
    fun getSelectedTextbook(): Flow<TextbookEntity?>

    @Query("UPDATE textbooks SET isSelected = CASE WHEN id = :selectedId THEN 1 ELSE 0 END")
    suspend fun setSelectedTextbook(selectedId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTextbooks(textbooks: List<TextbookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTextbook(textbook: TextbookEntity)

    @Update
    suspend fun updateTextbook(textbook: TextbookEntity)

    @Query("SELECT COUNT(*) FROM textbooks")
    suspend fun getCount(): Int

    @Query("DELETE FROM textbooks")
    suspend fun deleteAllTextbooks()
}
