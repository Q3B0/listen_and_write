package com.codex.listenandwrite.di

import android.content.Context
import com.codex.listenandwrite.data.local.AppDatabase
import com.codex.listenandwrite.data.local.PreferencesManager
import com.codex.listenandwrite.data.repository.DictationRepository
import com.codex.listenandwrite.data.repository.MistakeRepository
import com.codex.listenandwrite.data.repository.WordRepository
import com.codex.listenandwrite.tts.TtsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppContainer(val context: Context) {
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(context).also { db ->
            CoroutineScope(Dispatchers.IO).launch {
                db.prepopulate(context)
            }
        }
    }

    val preferencesManager: PreferencesManager by lazy {
        PreferencesManager(context)
    }

    val wordRepository: WordRepository by lazy {
        WordRepository(
            textbookDao = database.textbookDao(),
            unitDao = database.unitDao(),
            wordDao = database.wordDao(),
            wordBookDao = database.wordBookDao()
        )
    }

    val dictationRepository: DictationRepository by lazy {
        DictationRepository(
            dictationDao = database.dictationDao()
        )
    }

    val mistakeRepository: MistakeRepository by lazy {
        MistakeRepository(
            mistakeDao = database.mistakeDao()
        )
    }

    val ttsManager: TtsManager by lazy {
        TtsManager.getInstance().apply {
            initialize(context)
        }
    }
}
