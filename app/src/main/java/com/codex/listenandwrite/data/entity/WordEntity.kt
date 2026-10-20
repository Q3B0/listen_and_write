package com.codex.listenandwrite.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = UnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["unitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["unitId"]), Index(value = ["word"])]
)
data class WordEntity(
    @PrimaryKey
    val id: String,
    val unitId: String,
    val word: String,
    val phonetic: String,
    val translation: String,
    val exampleSentence: String? = null,
    val audioUrl: String? = null,
    val difficulty: Int = 1
)
