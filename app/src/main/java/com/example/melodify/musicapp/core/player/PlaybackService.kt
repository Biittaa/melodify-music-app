package com.melodify.musicapp.core.player

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : Service() {

    @Inject
    lateinit var exoPlayer: ExoPlayer

    private var mediaSession: MediaSessionCompat? = null
    private var mediaSessionConnector: MediaSessionConnector? = null
    private var notificationManager: MelodifyNotificationManager? = null

    private val binder = MusicBinder()

    inner class MusicBinder : Binder() {
        fun getService(): PlaybackService = this@PlaybackService
    }

    override fun onCreate() {
        super.onCreate()
        
        mediaSession = MediaSessionCompat(this, "MelodifyMediaSession").apply {
            isActive = true
        }
        
        mediaSession?.let { session ->
            mediaSessionConnector = MediaSessionConnector(session)
            mediaSessionConnector?.setPlayer(exoPlayer)
        }

        notificationManager = MelodifyNotificationManager(
            this,
            exoPlayer
        ) { notificationId, notification, ongoing ->
            if (ongoing) {
                startForeground(notificationId, notification)
            } else {
                // STOP_FOREGROUND_DETACH is sometimes problematic. Use STOP_FOREGROUND_REMOVE or false.
                stopForeground(false)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.release()
        exoPlayer.release()
        notificationManager = null
    }
}
