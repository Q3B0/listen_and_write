package com.codex.listenandwrite.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "textbooks")
data class TextbookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val publisher: String,
    val grade: Int,
    val semester: Int,
    val coverStyle: String = "orange",
    val unitCount: Int = 0,
    val wordCount: Int = 0,
    val isCustom: Boolean = false,
    val isSelected: Boolean = false
)
