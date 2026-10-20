package com.codex.listenandwrite.data.local

import android.content.Context
import android.util.Log
import com.codex.listenandwrite.data.entity.MistakeRecordEntity
import com.codex.listenandwrite.data.entity.TextbookEntity
import com.codex.listenandwrite.data.entity.UnitEntity
import com.codex.listenandwrite.data.entity.WordBookEntity
import com.codex.listenandwrite.data.entity.WordBookItemEntity
import com.codex.listenandwrite.data.entity.WordEntity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

object TextbookImporter {
    private const val TAG = "TextbookImporter"
    const val DEFAULT_TEXTBOOK_ID = "flyabc_3a" // 2024新版小学三年级上册

    data class IndexJson(
        val description: String?,
        val source: String?,
        val stages: List<String>?,
        val books: List<BookMeta>?
    )

    data class BookMeta(
        val id: String,
        val flyabcId: String?,
        val title: String,
        val stage: String,
        val grade: Int,
        val words: Int,
        val units: Int
    )

    data class TextbookDetailJson(
        val id: String,
        val flyabcId: String?,
        val title: String,
        val shortName: String?,
        val stage: String?,
        val grade: Int?,
        val edition: String?,
        val unitCount: Int?,
        val wordCount: Int?,
        val units: List<UnitJson>?
    )

    data class UnitJson(
        val id: String,
        val unitNumber: Int?,
        val title: String,
        val wordCount: Int?,
        val words: List<WordJson>?
    )

    data class WordJson(
        val en: String,
        val zh: String,
        val phonetic: String?,
        val isCore: Boolean?
    )

    /**
     * Clear existing word data and import fresh vocabulary from flyabc.fun (assets/textbooks/)
     */
    suspend fun importFromAssets(
        context: Context,
        database: AppDatabase,
        forceReload: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val textbookDao = database.textbookDao()
        val unitDao = database.unitDao()
        val wordDao = database.wordDao()
        val mistakeDao = database.mistakeDao()
        val wordBookDao = database.wordBookDao()

        val existingCount = textbookDao.getCount()
        val hasFlyabc3a = textbookDao.getTextbookById(DEFAULT_TEXTBOOK_ID) != null

        if (!forceReload && existingCount >= 19 && hasFlyabc3a) {
            Log.d(TAG, "FlyABC textbooks already loaded ($existingCount books). Skipping.")
            return@withContext
        }

        Log.d(TAG, "Clearing old vocabulary and importing FlyABC 2024 vocabulary...")

        // Clear existing data as requested
        wordDao.deleteAllWords()
        unitDao.deleteAllUnits()
        textbookDao.deleteAllTextbooks()
        mistakeDao.deleteAllMistakes()

        val gson = Gson()
        val assetManager = context.assets

        // 1. Read index.json
        val indexFile = "textbooks/index.json"
        val indexJson: IndexJson = try {
            assetManager.open(indexFile).use { inputStream ->
                InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                    gson.fromJson(reader, IndexJson::class.java)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read $indexFile", e)
            return@withContext
        }

        val bookList = indexJson.books ?: return@withContext

        val textbooks = mutableListOf<TextbookEntity>()
        val allUnits = mutableListOf<UnitEntity>()
        val allWords = mutableListOf<WordEntity>()

        for (meta in bookList) {
            val fileName = "textbooks/${meta.id}.json"
            val detail: TextbookDetailJson = try {
                assetManager.open(fileName).use { inputStream ->
                    InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                        gson.fromJson(reader, TextbookDetailJson::class.java)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to read textbook file: $fileName", e)
                continue
            }

            val grade = detail.grade ?: meta.grade
            val semester = parseSemester(meta.id)
            val publisher = when (meta.stage) {
                "primary" -> "人教版 PEP (2024新版)"
                "junior" -> "人教版初中英语 (新教材)"
                "senior" -> "人教版高中英语 (新课标)"
                else -> "萤光小语 flyabc.fun"
            }
            val coverStyle = getCoverStyle(grade, semester, meta.stage)

            var bookWordCount = 0
            val bookUnits = detail.units ?: emptyList()

            for ((unitIdx, unit) in bookUnits.withIndex()) {
                val unitNum = unit.unitNumber ?: (unitIdx + 1)
                val unitWords = unit.words ?: emptyList()
                val unitEntityId = "${meta.id}_${unit.id}"

                val unitEntity = UnitEntity(
                    id = unitEntityId,
                    textbookId = meta.id,
                    unitNumber = unitNum,
                    title = if (unit.title.isNotBlank()) unit.title else "Unit $unitNum",
                    wordCount = unitWords.size
                )
                allUnits.add(unitEntity)

                for ((wordIdx, wordItem) in unitWords.withIndex()) {
                    val wordEntity = WordEntity(
                        id = "${unitEntityId}_$wordIdx",
                        unitId = unitEntityId,
                        word = wordItem.en.trim(),
                        phonetic = wordItem.phonetic?.trim() ?: "",
                        translation = wordItem.zh.trim(),
                        exampleSentence = null,
                        audioUrl = null,
                        difficulty = if (wordItem.isCore == true) 2 else 1
                    )
                    allWords.add(wordEntity)
                }

                bookWordCount += unitWords.size
            }

            val textbookEntity = TextbookEntity(
                id = meta.id,
                title = meta.title,
                publisher = publisher,
                grade = grade,
                semester = semester,
                coverStyle = coverStyle,
                unitCount = bookUnits.size,
                wordCount = bookWordCount,
                isSelected = (meta.id == DEFAULT_TEXTBOOK_ID)
            )
            textbooks.add(textbookEntity)
        }

        // Batch insert new clean textbooks, units, and words
        textbookDao.insertTextbooks(textbooks)
        unitDao.insertUnits(allUnits)
        wordDao.insertWords(allWords)

        // Make sure DEFAULT_TEXTBOOK_ID is selected
        textbookDao.setSelectedTextbook(DEFAULT_TEXTBOOK_ID)

        // Insert sample mistakes from 3a for demonstration
        val sampleMistakes = listOf(
            MistakeRecordEntity(wordId = "flyabc_3a_u1_0", word = "name", phonetic = "", translation = "名字", unitTitle = "Unit 1 Making friends", mistakeCount = 2, isMastered = false),
            MistakeRecordEntity(wordId = "flyabc_3a_u1_1", word = "friend", phonetic = "", translation = "朋友", unitTitle = "Unit 1 Making friends", mistakeCount = 3, isMastered = false),
            MistakeRecordEntity(wordId = "flyabc_3a_u3_1", word = "dog", phonetic = "", translation = "狗", unitTitle = "Unit 3 Our animal friends", mistakeCount = 1, isMastered = false),
            MistakeRecordEntity(wordId = "flyabc_3a_u4_0", word = "apple", phonetic = "", translation = "苹果", unitTitle = "Unit 4 Plants around us", mistakeCount = 1, isMastered = true)
        )
        sampleMistakes.forEach { mistakeDao.insertOrUpdate(it) }

        // Initialize custom word book if empty
        val sampleWordBook = wordBookDao.getItemsForBook("wb_sample")
        if (sampleWordBook.isEmpty()) {
            val sampleBook = WordBookEntity(
                id = "wb_sample",
                title = "2024新版三年级核心速记",
                description = "开学前高频核心词汇",
                wordCount = 5,
                isCustom = true
            )
            val sampleItems = listOf(
                WordBookItemEntity(wordBookId = "wb_sample", word = "friend", phonetic = "", translation = "朋友", unitName = "Unit 1"),
                WordBookItemEntity(wordBookId = "wb_sample", word = "smile", phonetic = "", translation = "微笑", unitName = "Unit 1"),
                WordBookItemEntity(wordBookId = "wb_sample", word = "animal", phonetic = "", translation = "动物", unitName = "Unit 3"),
                WordBookItemEntity(wordBookId = "wb_sample", word = "apple", phonetic = "", translation = "苹果", unitName = "Unit 4"),
                WordBookItemEntity(wordBookId = "wb_sample", word = "colour", phonetic = "", translation = "颜色", unitName = "Unit 5")
            )
            wordBookDao.insertWordBook(sampleBook)
            wordBookDao.insertWordBookItems(sampleItems)
        }

        Log.d(TAG, "FlyABC import completed: ${textbooks.size} textbooks, ${allUnits.size} units, ${allWords.size} words.")
    }

    private fun parseSemester(id: String): Int {
        return when {
            id.contains("3a") || id.contains("4a") || id.contains("5a") || id.contains("6a") || id.contains("7a") || id.contains("8a") || id.contains("9a") -> 1
            id.contains("3b") || id.contains("4b") || id.contains("5b") || id.contains("6b") || id.contains("7b") || id.contains("8b") || id.contains("9b") -> 2
            id.contains("h1") -> 1
            id.contains("h2") -> 2
            id.contains("h3") -> 3
            id.contains("hx1") -> 1
            id.contains("hx2") -> 2
            id.contains("hx3") -> 3
            id.contains("hx4") -> 4
            else -> 1
        }
    }

    private fun getCoverStyle(grade: Int, semester: Int, stage: String?): String {
        return when (grade) {
            3 -> if (semester == 1) "orange" else "blue"
            4 -> if (semester == 1) "green" else "yellow"
            5 -> if (semester == 1) "purple" else "pink"
            6 -> if (semester == 1) "teal" else "indigo"
            7 -> if (semester == 1) "blue" else "cyan"
            8 -> if (semester == 1) "green" else "teal"
            9 -> "orange"
            10 -> if (semester == 1) "blue" else "indigo"
            11 -> if (semester == 1) "purple" else "violet"
            else -> "teal"
        }
    }
}
