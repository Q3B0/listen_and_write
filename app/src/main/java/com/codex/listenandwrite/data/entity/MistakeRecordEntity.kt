package com.codex.listenandwrite.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mistake_records",
    indices = [Index(value = ["word"], unique = true)]
)
data class MistakeRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: String? = null,
    val word: String,
    val phonetic: String,
    val translation: String,
    val unitTitle: String = "",
    val mistakeCount: Int = 1,
    val lastMistakeTime: Long = System.currentTimeMillis(),
    val isMastered: Boolean = false,
    val isFavorite: Boolean = false
)
