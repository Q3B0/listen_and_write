package com.codex.listenandwrite

import android.app.Application
import com.codex.listenandwrite.di.AppContainer

class ListenAndWriteApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
