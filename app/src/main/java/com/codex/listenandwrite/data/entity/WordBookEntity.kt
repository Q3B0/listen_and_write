package com.codex.listenandwrite.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_books")
data class WordBookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val wordCount: Int = 0,
    val isCustom: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
