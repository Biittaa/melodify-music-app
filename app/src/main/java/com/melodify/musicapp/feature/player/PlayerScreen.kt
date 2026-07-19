package com.melodify.musicapp.feature.player

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.melodify.R
import com.melodify.musicapp.ui.components.MelodifyTopBar
import com.melodify.musicapp.ui.theme.MelodifyColors
import com.melodify.musicapp.ui.theme.Space16
import com.melodify.musicapp.ui.theme.Space24
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PlayerScreen(
    navController: NavController,
    songId: String,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Load song on start
    LaunchedEffect(songId) {
        viewModel.loadSong(songId)
    }

    // Dynamic background color from palette
    val backgroundColor = remember(uiState.currentSong) {
        // Extract dominant color from cover
        // Using Palette API - simplified here
        Color(0xFF1A1A1A)
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            MelodifyTopBar(
                title = stringResource(R.string.now_playing),
                showActions = false,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        PlayerContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun PlayerContent(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState,
    onEvent: (PlayerEvent) -> Unit
) {
    val song = uiState.currentSong ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Space16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // CD Player View
        CDPlayerView(
            coverUrl = song.coverUrl,
            isPlaying = uiState.isPlaying,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        )

        Spacer(modifier = Modifier.Companion.height(Space24))

        // Song Info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artistName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.Companion.height(Space16))

        // Progress Bar
        PlayerProgressBar(
            currentPosition = uiState.currentPosition,
            duration = uiState.duration,
            onSeek = { position -> onEvent(PlayerEvent.OnSeek(position)) }
        )

        Spacer(modifier = Modifier.Companion.height(Space16))

        // Controls Row
        PlayerControls(
            isPlaying = uiState.isPlaying,
            onPlayPause = { onEvent(PlayerEvent.OnPlayPause) },
            onNext = { onEvent(PlayerEvent.OnNext) },
            onPrevious = { onEvent(PlayerEvent.OnPrevious) },
            onShuffleToggle = { onEvent(PlayerEvent.OnToggleShuffle) },
            onRepeatToggle = { onEvent(PlayerEvent.OnToggleRepeat) },
            isShuffleEnabled = uiState.isShuffleEnabled,
            repeatMode = uiState.repeatMode
        )

        Spacer(modifier = Modifier.Companion.height(Space16))

        // Bottom Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Playback Speed
            IconButton(
                onClick = { onEvent(PlayerEvent.OnShowSpeedDialog) }
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = stringResource(R.string.playback_speed),
                    tint = Color.White
                )
            }

            // Sleep Timer
            IconButton(
                onClick = { onEvent(PlayerEvent.OnShowSleepTimer) }
            ) {
                Icon(
                    imageVector = Icons.Default.Bedtime,
                    contentDescription = stringResource(R.string.sleep_timer),
                    tint = Color.White
                )
            }

            // Like
            IconButton(
                onClick = { onEvent(PlayerEvent.OnToggleLike) }
            ) {
                Icon(
                    imageVector = if (uiState.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.like),
                    tint = if (uiState.isLiked) MelodifyColors().primary else Color.White
                )
            }

            // Share
            IconButton(
                onClick = { onEvent(PlayerEvent.OnShare) }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share),
                    tint = Color.White
                )
            }
        }

        // Sleep Timer Dialog
        if (uiState.showSleepTimer) {
            SleepTimerDialog(
                onDismiss = { onEvent(PlayerEvent.OnDismissSleepTimer) },
                onSetTimer = { minutes -> onEvent(PlayerEvent.OnSetSleepTimer(minutes)) }
            )
        }

        // Speed Dialog
        if (uiState.showSpeedDialog) {
            PlaybackSpeedDialog(
                currentSpeed = uiState.playbackSpeed,
                onDismiss = { onEvent(PlayerEvent.OnDismissSpeedDialog) },
                onSpeedSelected = { speed -> onEvent(PlayerEvent.OnChangeSpeed(speed)) }
            )
        }
    }
}

@Composable
fun CDPlayerView(
    coverUrl: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    var rotation by remember { mutableFloatStateOf(0f) }

    // Rotation animation
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(16) // ~60 FPS
            rotation += 0.5f
            if (rotation >= 360f) rotation = 0f
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer CD ring
        Canvas(
            modifier = Modifier
                .size(280.dp)
                .rotate(rotation)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            // CD Background
            drawCircle(
                color = Color.DarkGray,
                radius = radius
            )

            // CD Shine effect
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = radius * 0.9f,
                center = center
            )

            // Inner circles
            drawCircle(
                color = Color.Gray,
                radius = radius * 0.7f,
                center = center
            )

            drawCircle(
                color = Color.DarkGray,
                radius = radius * 0.3f,
                center = center
            )

            drawCircle(
                color = Color.Black,
                radius = radius * 0.15f,
                center = center
            )

            // CD reflection lines
            for (i in 0..11) {
                val angle = (i * 30).toDouble()
                val startX = center.x + (radius * 0.2f * cos(Math.toRadians(angle))).toFloat()
                val startY = center.y + (radius * 0.2f * sin(Math.toRadians(angle))).toFloat()
                val endX = center.x + (radius * 0.8f * cos(Math.toRadians(angle))).toFloat()
                val endY = center.y + (radius * 0.8f * sin(Math.toRadians(angle))).toFloat()

                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )
            }
        }

        // Album Cover on top
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Album Cover",
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun PlayerProgressBar(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { onSeek(it.toLong()) },
            valueRange = 0f..duration.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = MelodifyColors().primary,
                activeTrackColor = MelodifyColors().primary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPosition),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = formatTime(duration),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onShuffleToggle: () -> Unit,
    onRepeatToggle: () -> Unit,
    isShuffleEnabled: Boolean,
    repeatMode: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onShuffleToggle,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = stringResource(R.string.shuffle),
                tint = if (isShuffleEnabled) MelodifyColors().primary else Color.White.copy(alpha = 0.5f)
            )
        }

        IconButton(
            onClick = onPrevious,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = stringResource(R.string.previous),
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        FloatingActionButton(
            onClick = onPlayPause,
            containerColor = MelodifyColors().primary,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) stringResource(R.string.pause) else stringResource(R.string.play),
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        IconButton(
            onClick = onNext,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = stringResource(R.string.next),
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        IconButton(
            onClick = onRepeatToggle,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = when (repeatMode) {
                    1 -> Icons.Default.RepeatOne
                    else -> Icons.Default.Repeat
                },
                contentDescription = stringResource(R.string.repeat),
                tint = if (repeatMode > 0) MelodifyColors().primary else Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SleepTimerDialog(
    onDismiss: () -> Unit,
    onSetTimer: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.sleep_timer)) },
        text = {
            Column {
                listOf(5, 10, 15, 30, 45, 60).forEach { minutes ->
                    TextButton(
                        onClick = {
                            onSetTimer(minutes)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("$minutes ${stringResource(R.string.minutes)}")
                    }
                }
                TextButton(
                    onClick = {
                        onSetTimer(0)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.turn_off))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun PlaybackSpeedDialog(
    currentSpeed: Float,
    onDismiss: () -> Unit,
    onSpeedSelected: (Float) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.playback_speed)) },
        text = {
            Column {
                listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                    TextButton(
                        onClick = {
                            onSpeedSelected(speed)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${speed}x")
                            if (speed == currentSpeed) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MelodifyColors().primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

private fun formatTime(millis: Long): String {
    val seconds = millis / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}