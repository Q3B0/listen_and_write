package com.codex.listenandwrite.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dictation_items",
    foreignKeys = [
        ForeignKey(
            entity = DictationSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class DictationItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val word: String,
    val phonetic: String,
    val translation: String,
    val userInput: String,
    val isCorrect: Boolean,
    val orderIndex: Int
)
