package com.codex.listenandwrite.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "word_book_items",
    foreignKeys = [
        ForeignKey(
            entity = WordBookEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordBookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["wordBookId"])]
)
data class WordBookItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordBookId: String,
    val word: String,
    val phonetic: String,
    val translation: String,
    val unitName: String? = null
)
