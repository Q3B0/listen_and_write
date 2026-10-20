package com.codex.listenandwrite.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codex.listenandwrite.data.entity.WordBookEntity
import com.codex.listenandwrite.data.entity.WordBookItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordBookDao {
    @Query("SELECT * FROM word_books ORDER BY createdAt DESC")
    fun getAllWordBooks(): Flow<List<WordBookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordBook(book: WordBookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordBookItems(items: List<WordBookItemEntity>)

    @Query("SELECT * FROM word_book_items WHERE wordBookId = :bookId")
    suspend fun getItemsForBook(bookId: String): List<WordBookItemEntity>

    @Query("DELETE FROM word_books WHERE id = :bookId")
    suspend fun deleteWordBook(bookId: String)
}
