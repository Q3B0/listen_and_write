package com.codex.listenandwrite

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class JsonImportTest {

    @Test
    fun parseJsonWordBook_validContent_success() {
        val json = """
        {
          "title": "五年级核心冲刺",
          "description": "单元高频考点词汇",
          "words": [
            {"word": "family", "phonetic": "['fæməli]", "translation": "家庭"},
            {"word": "giraffe", "phonetic": "[dʒəˈrɑːf]", "translation": "长颈鹿"}
          ]
        }
        """.trimIndent()

        val gson = Gson()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val map: Map<String, Any> = gson.fromJson(json, type)

        assertEquals("五年级核心冲刺", map["title"])
        assertEquals("单元高频考点词汇", map["description"])

        val wordsList = map["words"] as? List<Map<String, String>>
        assertNotNull(wordsList)
        assertEquals(2, wordsList?.size)
        assertEquals("family", wordsList?.get(0)?.get("word"))
        assertEquals("家庭", wordsList?.get(0)?.get("translation"))
    }

    @Test
    fun parsePepRepositoryTextbookFormat_validContent_success() {
        val json = """
        {
          "id": "pep-primary-g4-s1",
          "title": "人教版小学英语 · 四年级上",
          "stage": "primary",
          "edition": "PEP",
          "units": [
            {
              "id": "u1",
              "title": "Unit 1",
              "words": [
                {"en": "classroom", "zh": "n. 教室", "phonetic": "/ˈklæsruːm/"},
                {"en": "window", "zh": "n. 窗户", "phonetic": "/ˈwɪndoʊ/"}
              ]
            },
            {
              "id": "u2",
              "title": "Unit 2",
              "words": [
                {"en": "schoolbag", "zh": "n. 书包", "phonetic": "/ˈskuːlbæɡ/"}
              ]
            }
          ]
        }
        """.trimIndent()

        val gson = Gson()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val map: Map<String, Any> = gson.fromJson(json, type)

        assertEquals("pep-primary-g4-s1", map["id"])
        assertEquals("人教版小学英语 · 四年级上", map["title"])

        val unitsList = map["units"] as? List<Map<String, Any>>
        assertNotNull(unitsList)
        assertEquals(2, unitsList?.size)

        val u1Words = unitsList?.get(0)?.get("words") as? List<Map<String, Any>>
        assertNotNull(u1Words)
        assertEquals(2, u1Words?.size)
        assertEquals("classroom", u1Words?.get(0)?.get("en"))
        assertEquals("n. 教室", u1Words?.get(0)?.get("zh"))
    }

    @Test
    fun parseFlyabcTextbookFormat_validContent_success() {
        val json = """
        {
          "id": "flyabc_3a",
          "flyabcId": "3a",
          "title": "人教版 PEP 小学英语 · 三年级上册 (2024新版)",
          "stage": "primary",
          "grade": 3,
          "edition": "2024",
          "unitCount": 6,
          "wordCount": 108,
          "units": [
            {
              "id": "u1",
              "unitNumber": 1,
              "title": "Unit 1 Making friends",
              "wordCount": 15,
              "words": [
                {"en": "name", "zh": "名字", "phonetic": "", "isCore": false},
                {"en": "ear", "zh": "耳朵", "phonetic": "", "isCore": true}
              ]
            }
          ]
        }
        """.trimIndent()

        val gson = Gson()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val map: Map<String, Any> = gson.fromJson(json, type)

        assertEquals("flyabc_3a", map["id"])
        assertEquals("2024", map["edition"])

        val unitsList = map["units"] as? List<Map<String, Any>>
        assertNotNull(unitsList)
        assertEquals(1, unitsList?.size)

        val u1Words = unitsList?.get(0)?.get("words") as? List<Map<String, Any>>
        assertNotNull(u1Words)
        assertEquals(2, u1Words?.size)
        assertEquals("name", u1Words?.get(0)?.get("en"))
        assertEquals("名字", u1Words?.get(0)?.get("zh"))
    }
}
