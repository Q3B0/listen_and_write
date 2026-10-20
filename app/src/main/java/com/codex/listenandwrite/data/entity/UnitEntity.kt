package com.codex.listenandwrite.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "units",
    foreignKeys = [
        ForeignKey(
            entity = TextbookEntity::class,
            parentColumns = ["id"],
            childColumns = ["textbookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["textbookId"])]
)
data class UnitEntity(
    @PrimaryKey
    val id: String,
    val textbookId: String,
    val unitNumber: Int,
    val title: String,
    val wordCount: Int = 0
)
