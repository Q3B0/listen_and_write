package com.codex.listenandwrite.data.repository

import com.codex.listenandwrite.data.dao.TextbookDao
import com.codex.listenandwrite.data.dao.UnitDao
import com.codex.listenandwrite.data.dao.WordBookDao
import com.codex.listenandwrite.data.dao.WordDao
import com.codex.listenandwrite.data.entity.TextbookEntity
import com.codex.listenandwrite.data.entity.UnitEntity
import com.codex.listenandwrite.data.entity.WordBookEntity
import com.codex.listenandwrite.data.entity.WordBookItemEntity
import com.codex.listenandwrite.data.entity.WordEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class WordRepository(
    private val textbookDao: TextbookDao,
    private val unitDao: UnitDao,
    private val wordDao: WordDao,
    private val wordBookDao: WordBookDao
) {
    fun getAllTextbooks(): Flow<List<TextbookEntity>> = textbookDao.getAllTextbooks()

    fun getSelectedTextbook(): Flow<TextbookEntity?> = textbookDao.getSelectedTextbook()

    suspend fun setSelectedTextbook(id: String) {
        textbookDao.setSelectedTextbook(id)
    }

    suspend fun getTextbookById(id: String): TextbookEntity? {
        val actualId = if (id == "pep_3_2" || id == "pep-primary-g3-s2") "flyabc_3a" else id
        return textbookDao.getTextbookById(actualId)
    }

    fun getUnitsForTextbook(textbookId: String): Flow<List<UnitEntity>> {
        val actualId = if (textbookId == "pep_3_2" || textbookId == "pep-primary-g3-s2") "flyabc_3a" else textbookId
        return unitDao.getUnitsForTextbook(actualId)
    }

    suspend fun getUnitsListForTextbook(textbookId: String): List<UnitEntity> {
        val actualId = if (textbookId == "pep_3_2" || textbookId == "pep-primary-g3-s2") "flyabc_3a" else textbookId
        return unitDao.getUnitsListForTextbook(actualId)
    }

    suspend fun getWordsForUnits(unitIds: List<String>): List<WordEntity> =
        wordDao.getWordsForUnits(unitIds)

    fun getAllWordBooks(): Flow<List<WordBookEntity>> = wordBookDao.getAllWordBooks()

    suspend fun getItemsForWordBook(bookId: String): List<WordBookItemEntity> =
        wordBookDao.getItemsForBook(bookId)

    suspend fun importWordBookFromJson(jsonString: String): Result<WordBookEntity> {
        return runCatching {
            val gson = Gson()
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            val rawData: Map<String, Any> = gson.fromJson(jsonString, mapType)

            val title = rawData["title"] as? String ?: "导入词包"
            val description = rawData["description"] as? String ?: "用户自定义导入"
            val bookId = "wb_${UUID.randomUUID()}"
            val items = mutableListOf<WordBookItemEntity>()

            if (rawData.containsKey("units")) {
                // pep-english-words repository textbook format
                val unitsRaw = rawData["units"] as? List<Map<String, Any>> ?: emptyList()
                for (unit in unitsRaw) {
                    val unitTitle = unit["title"] as? String ?: "Unit"
                    val wordsList = unit["words"] as? List<Map<String, Any>> ?: emptyList()
                    for (w in wordsList) {
                        val en = (w["en"] ?: w["word"]) as? String ?: ""
                        val zh = (w["zh"] ?: w["translation"]) as? String ?: ""
                        val phonetic = w["phonetic"] as? String ?: ""
                        if (en.isNotBlank()) {
                            items.add(
                                WordBookItemEntity(
                                    wordBookId = bookId,
                                    word = en.trim(),
                                    phonetic = phonetic.trim(),
                                    translation = zh.trim(),
                                    unitName = unitTitle
                                )
                            )
                        }
                    }
                }
            } else {
                // Standard custom wordbook format
                val wordsRaw = rawData["words"] as? List<Map<String, String>> ?: emptyList()
                for (item in wordsRaw) {
                    val w = item["word"] ?: item["en"] ?: ""
                    if (w.isNotBlank()) {
                        items.add(
                            WordBookItemEntity(
                                wordBookId = bookId,
                                word = w.trim(),
                                phonetic = item["phonetic"] ?: "",
                                translation = item["translation"] ?: item["zh"] ?: "",
                                unitName = item["unit"]
                            )
                        )
                    }
                }
            }

            val wordBook = WordBookEntity(
                id = bookId,
                title = title,
                description = description,
                wordCount = items.size,
                isCustom = true
            )

            wordBookDao.insertWordBook(wordBook)
            wordBookDao.insertWordBookItems(items)
            wordBook
        }
    }
}
