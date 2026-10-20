package com.codex.listenandwrite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.codex.listenandwrite.ui.navigation.AppNavGraph
import com.codex.listenandwrite.ui.theme.ListenAndWriteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as ListenAndWriteApp).container

        setContent {
            ListenAndWriteTheme {
                AppNavGraph(container = appContainer)
            }
        }
    }
}
