package com.melodify.musicapp.feature.main

import android.Manifest
import android.os.Build
import android.os.Bundle
//import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.melodify.musicapp.domain.repository.SettingsRepository
import com.melodify.musicapp.ui.theme.MelodifyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.CompositionLocalProvider
@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContent {
            val settings by settingsRepository.getSettingsFlow().collectAsState(initial = null)

            // درخواست مجوز دسترسی به فایل‌های صوتی
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

            LaunchedEffect(settings?.language) {
                settings?.language?.let { language ->
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(language)
                    )
                }
            }

            val layoutDirection = if (settings?.language == "fa") {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }
            val currentDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides object : androidx.compose.ui.unit.Density {
                    override val density: Float
                        get() = currentDensity.density

                    override val fontScale: Float
                        get() = settings?.fontScale ?: 1f
                }
            ) {
                MelodifyTheme(
                    darkTheme = settings?.darkMode ?: false
                ) {
                    MainScreen()
                }
            }

//            MelodifyTheme(
//                darkTheme = settings?.darkMode ?: false
//            ) {
//                MainScreen()
//            }
        }
    }
}