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
import com.melodify.musicapp.core.common.Constants
import kotlinx.coroutines.*

class MelodifyNotificationManager(
    private val context: Context,
    private val player: Player,
    private val onNotificationPosted: (Int, android.app.Notification, Boolean) -> Unit
) {
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val notificationManager: PlayerNotificationManager

    init {
        notificationManager = PlayerNotificationManager.Builder(
            context,
            Constants.PLAYER_NOTIFICATION_ID,
            Constants.PLAYER_CHANNEL_ID
        )
            .setChannelNameResourceId(R.string.app_name)
            .setMediaDescriptionAdapter(DescriptionAdapter())
            .setNotificationListener(NotificationListener())
            .build().apply {
                setPlayer(player)
                setUseNextAction(true)
                setUsePreviousAction(true)
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
            val uri = player.currentMediaItem?.mediaMetadata?.artworkUri
            if (uri != null) {
                serviceScope.launch {
                    val loader = ImageLoader(context)
                    val request = ImageRequest.Builder(context).data(uri).build()
                    val result = loader.execute(request)
                    if (result.drawable is BitmapDrawable) {
                        callback.onBitmap((result.drawable as BitmapDrawable).bitmap)
                    }
                }
            }
            return null
        }
    }

    private inner class NotificationListener : PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: android.app.Notification,
            ongoing: Boolean
        ) {
            onNotificationPosted(notificationId, notification, ongoing)
        }
    }
}
