package com.melodify.musicapp.core.player

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.melodify.musicapp.R

class MelodifyNotificationManager(
    private val context: Context,
    private val player: Player,
    private val notificationListener: PlayerNotificationManager.NotificationListener
) {
    private val notificationManager: PlayerNotificationManager

    init {
        notificationManager = PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            CHANNEL_ID
        )
            .setNotificationListener(notificationListener)
            .setMediaDescriptionAdapter(DescriptionAdapter())
            .build().apply {
                setPlayer(player)
            }
    }

    private inner class DescriptionAdapter : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence =
            player.currentMediaItem?.mediaMetadata?.title ?: "Unknown"

        override fun createCurrentContentIntent(player: Player): PendingIntent? = null

        override fun getCurrentContentText(player: Player): CharSequence =
            player.currentMediaItem?.mediaMetadata?.artist ?: "Unknown Artist"

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            // بارگذاری آیکون از URL (اختیاری - در اینجا از لوگوی برنامه استفاده میکنیم)
            return null 
        }
    }

    companion object {
        const val CHANNEL_ID = "melodify_music_channel"
        const val NOTIFICATION_ID = 1
    }
}