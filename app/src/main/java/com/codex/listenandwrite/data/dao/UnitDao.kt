package com.codex.listenandwrite.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codex.listenandwrite.data.entity.UnitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {
    @Query("SELECT * FROM units WHERE textbookId = :textbookId ORDER BY unitNumber ASC")
    fun getUnitsForTextbook(textbookId: String): Flow<List<UnitEntity>>

    @Query("SELECT * FROM units WHERE textbookId = :textbookId ORDER BY unitNumber ASC")
    suspend fun getUnitsListForTextbook(textbookId: String): List<UnitEntity>

    @Query("SELECT * FROM units WHERE id IN (:unitIds)")
    suspend fun getUnitsByIds(unitIds: List<String>): List<UnitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnits(units: List<UnitEntity>)

    @Query("DELETE FROM units WHERE textbookId = :textbookId")
    suspend fun deleteByTextbook(textbookId: String)

    @Query("DELETE FROM units")
    suspend fun deleteAllUnits()
}
