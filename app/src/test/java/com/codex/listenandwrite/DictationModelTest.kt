package com.codex.listenandwrite

import com.codex.listenandwrite.domain.model.DictationWordItem
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DictationModelTest {

    @Test
    fun checkAnswer_exactMatch_returnsTrue() {
        val item = DictationWordItem(
            id = "w1",
            word = "family",
            phonetic = "['fæməli]",
            translation = "家庭"
        )
        assertTrue(item.checkAnswer("family"))
    }

    @Test
    fun checkAnswer_caseInsensitive_returnsTrue() {
        val item = DictationWordItem(
            id = "w1",
            word = "Family",
            phonetic = "['fæməli]",
            translation = "家庭"
        )
        assertTrue(item.checkAnswer("family"))
        assertTrue(item.checkAnswer("FAMILY"))
        assertTrue(item.checkAnswer("FaMiLy"))
    }

    @Test
    fun checkAnswer_surroundingSpaces_returnsTrue() {
        val item = DictationWordItem(
            id = "w1",
            word = "teacher",
            phonetic = "[ˈtiːtʃə(r)]",
            translation = "老师"
        )
        assertTrue(item.checkAnswer("  teacher  "))
    }

    @Test
    fun checkAnswer_apostropheNormalization_returnsTrue() {
        val item = DictationWordItem(
            id = "w1",
            word = "don't",
            phonetic = "[dəʊnt]",
            translation = "不"
        )
        assertTrue(item.checkAnswer("don’t")) // Curly apostrophe input
        assertTrue(item.checkAnswer("don't")) // Standard apostrophe input
    }

    @Test
    fun checkAnswer_typo_returnsFalse() {
        val item = DictationWordItem(
            id = "w1",
            word = "family",
            phonetic = "['fæməli]",
            translation = "家庭"
        )
        assertFalse(item.checkAnswer("famliy")) // Transposed letters
        assertFalse(item.checkAnswer("famly"))
        assertFalse(item.checkAnswer(""))
    }
}
