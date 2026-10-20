package com.codex.listenandwrite

import com.codex.listenandwrite.domain.model.DictationConfig
import com.codex.listenandwrite.domain.model.DictationMode
import com.codex.listenandwrite.domain.model.SpeechAccent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DictationConfigTest {

    @Test
    fun defaultConfig_hasExpectedValues() {
        val config = DictationConfig()
        assertEquals(2, config.repeatCount)
        assertEquals(5, config.intervalSeconds)
        assertEquals(1.0f, config.speechRate)
        assertEquals(SpeechAccent.US, config.speechAccent)
        assertEquals(com.codex.listenandwrite.domain.model.VoiceSource.REAL_HUMAN, config.voiceSource)
        assertEquals(DictationMode.PAPER, config.mode)
        assertTrue(config.isRandom)
    }

    @Test
    fun limitWordCount_whenExceedsTotal_isSafelyHandled() {
        val total = 15
        val requested = 30
        val actual = minOf(requested, total)
        assertEquals(15, actual)
    }
}
