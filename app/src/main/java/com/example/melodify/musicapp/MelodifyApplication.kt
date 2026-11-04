package com.melodify.musicapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for Melodify Music App
 * Enables Hilt dependency injection
 */
@HiltAndroidApp
class MelodifyApplication : Application()