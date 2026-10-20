package com.codex.listenandwrite.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val KEY_REPEAT_COUNT = intPreferencesKey("repeat_count")
        val KEY_INTERVAL_SECONDS = intPreferencesKey("interval_seconds")
        val KEY_SPEECH_RATE = floatPreferencesKey("speech_rate")
        val KEY_SPEECH_ACCENT = stringPreferencesKey("speech_accent") // "US" or "UK"
        val KEY_READ_CHINESE = booleanPreferencesKey("read_chinese")
        val KEY_DICTATION_MODE = stringPreferencesKey("dictation_mode") // "PAPER" or "INPUT"
        val KEY_WORD_COUNT = intPreferencesKey("word_count")
        val KEY_RANDOM_ORDER = booleanPreferencesKey("random_order")
        val KEY_SELECTED_TEXTBOOK = stringPreferencesKey("selected_textbook")
        val KEY_VOICE_SOURCE = stringPreferencesKey("voice_source") // "REAL_HUMAN" or "SYSTEM_TTS"
    }

    val repeatCount: Flow<Int> = context.dataStore.data.map { it[KEY_REPEAT_COUNT] ?: 2 }
    val intervalSeconds: Flow<Int> = context.dataStore.data.map { it[KEY_INTERVAL_SECONDS] ?: 5 }
    val speechRate: Flow<Float> = context.dataStore.data.map { it[KEY_SPEECH_RATE] ?: 1.0f }
    val speechAccent: Flow<String> = context.dataStore.data.map { it[KEY_SPEECH_ACCENT] ?: "US" }
    val readChinese: Flow<Boolean> = context.dataStore.data.map { it[KEY_READ_CHINESE] ?: false }
    val dictationMode: Flow<String> = context.dataStore.data.map { it[KEY_DICTATION_MODE] ?: "PAPER" }
    val wordCount: Flow<Int> = context.dataStore.data.map { it[KEY_WORD_COUNT] ?: 20 }
    val randomOrder: Flow<Boolean> = context.dataStore.data.map { it[KEY_RANDOM_ORDER] ?: true }
    val selectedTextbookId: Flow<String> = context.dataStore.data.map {
        val raw = it[KEY_SELECTED_TEXTBOOK] ?: "flyabc_3a"
        if (raw == "pep_3_2" || raw == "pep-primary-g3-s2") "flyabc_3a" else raw
    }
    val voiceSource: Flow<String> = context.dataStore.data.map { it[KEY_VOICE_SOURCE] ?: "REAL_HUMAN" }

    suspend fun setRepeatCount(count: Int) {
        context.dataStore.edit { it[KEY_REPEAT_COUNT] = count }
    }

    suspend fun setIntervalSeconds(seconds: Int) {
        context.dataStore.edit { it[KEY_INTERVAL_SECONDS] = seconds }
    }

    suspend fun setSpeechRate(rate: Float) {
        context.dataStore.edit { it[KEY_SPEECH_RATE] = rate }
    }

    suspend fun setSpeechAccent(accent: String) {
        context.dataStore.edit { it[KEY_SPEECH_ACCENT] = accent }
    }

    suspend fun setReadChinese(read: Boolean) {
        context.dataStore.edit { it[KEY_READ_CHINESE] = read }
    }

    suspend fun setDictationMode(mode: String) {
        context.dataStore.edit { it[KEY_DICTATION_MODE] = mode }
    }

    suspend fun setWordCount(count: Int) {
        context.dataStore.edit { it[KEY_WORD_COUNT] = count }
    }

    suspend fun setRandomOrder(random: Boolean) {
        context.dataStore.edit { it[KEY_RANDOM_ORDER] = random }
    }

    suspend fun setSelectedTextbook(id: String) {
        context.dataStore.edit { it[KEY_SELECTED_TEXTBOOK] = id }
    }

    suspend fun setVoiceSource(source: String) {
        context.dataStore.edit { it[KEY_VOICE_SOURCE] = source }
    }
}
