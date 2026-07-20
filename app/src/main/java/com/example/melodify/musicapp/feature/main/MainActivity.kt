package com.melodify.musicapp.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.melodify.musicapp.domain.repository.SettingsRepository
import com.melodify.musicapp.ui.theme.MelodifyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Observe settings flow to update UI dynamically
            val settings by settingsRepository.getSettingsFlow().collectAsState(initial = null)
            
            // RTL for Persian, LTR for English
            val layoutDirection = if (settings?.language == "fa") {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                MelodifyTheme(darkTheme = settings?.darkMode ?: false) {
                    MainScreen()
                }
            }
        }
    }
}
