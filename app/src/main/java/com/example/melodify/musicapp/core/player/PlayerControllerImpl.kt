package com.melodify.musicapp.core.player

import android.content.Context
import android.content.Intent
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.melodify.musicapp.domain.model.PlayerState
import com.melodify.musicapp.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerControllerImpl @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val downloadManager: MelodifyDownloadManager,
    @ApplicationContext private val context: Context
) : IPlayerController, Player.Listener {

    private val _playerState = MutableStateFlow(PlayerState(null, false, 0L, 0L, 0, false, 1f))
    private var currentPlaylist: List<Song> = emptyList()
    
    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null

    init {
        exoPlayer.addListener(this)
    }

    override fun play(song: Song) {
        playPlaylist(listOf(song), 0)
    }

    override fun playPlaylist(songs: List<Song>, startIndex: Int) {
        currentPlaylist = songs
        
        val intent = Intent(context, PlaybackService::class.java)
        context.startService(intent)

        val mediaItems = songs.map { song ->
            val localUri = downloadManager.getLocalFileUri(song.id)
            val uriToPlay = localUri ?: song.audioUrl
            MediaItem.Builder()
                .setUri(uriToPlay)
                .setMediaId(song.id)
                .build()
        }

        exoPlayer.setMediaItems(mediaItems)
        if (startIndex in songs.indices) {
            exoPlayer.seekTo(startIndex, 0L)
        }
        exoPlayer.prepare()
        exoPlayer.play()
        updateState()
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun next() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        } else if (exoPlayer.repeatMode == Player.REPEAT_MODE_ALL) {
            exoPlayer.seekTo(0, 0L)
        }
    }

    override fun previous() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        }
    }

    override fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    override fun setSpeed(speed: Float) {
        exoPlayer.setPlaybackSpeed(speed)
        updateState()
    }

    override fun toggleShuffle(enable: Boolean?) {
        exoPlayer.shuffleModeEnabled = enable ?: !exoPlayer.shuffleModeEnabled
        updateState()
    }

    override fun setRepeatMode(mode: Int) {
        exoPlayer.repeatMode = mode
        updateState()
    }

    override fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes > 0) {
            sleepTimerJob = CoroutineScope(Dispatchers.Main).launch {
                delay(minutes * 60 * 1000L)
                pause()
                sleepTimerJob = null
            }
        }
    }

    override fun getPlayerState(): Flow<PlayerState> = _playerState.asStateFlow()

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        updateState()
        if (isPlaying) {
            startProgressUpdate()
        } else {
            stopProgressUpdate()
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        updateState()
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        updateState()
    }

    private fun updateState() {
        val currentMediaItem = exoPlayer.currentMediaItem
        val song = currentPlaylist.find { it.id == currentMediaItem?.mediaId }
        
        _playerState.value = _playerState.value.copy(
            currentSong = song,
            isPlaying = exoPlayer.isPlaying,
            currentPosition = exoPlayer.currentPosition,
            duration = exoPlayer.duration.coerceAtLeast(0L),
            repeatMode = exoPlayer.repeatMode,
            shuffleEnabled = exoPlayer.shuffleModeEnabled,
            playbackSpeed = exoPlayer.playbackParameters.speed
        )
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                updateState()
                delay(1000)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }
}