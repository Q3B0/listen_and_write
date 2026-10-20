package com.codex.listenandwrite.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictation_sessions")
data class DictationSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val textbookTitle: String,
    val unitRange: String,
    val mode: String, // "PAPER" or "INPUT"
    val totalCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val accuracy: Float,
    val durationSeconds: Int
)
