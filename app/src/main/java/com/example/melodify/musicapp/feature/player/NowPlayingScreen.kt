package com.melodify.musicapp.feature.player

import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.melodify.musicapp.R
import com.melodify.musicapp.domain.model.PlayerState
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    onBackClick: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val playerState by viewModel.playerState.collectAsState()
    val isLiked by viewModel.isCurrentSongLiked.collectAsState()
    val song = playerState.currentSong ?: return

    var showSpeedDialog by remember { mutableStateOf(false) }
    var showTimerDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var backgroundColor by remember { mutableStateOf(Color.DarkGray) }
    val context = LocalContext.current

    LaunchedEffect(song.coverUrl) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(song.coverUrl)
            .allowHardware(false)
            .build()
        val result = loader.execute(request)
        if (result is SuccessResult) {
            val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
            bitmap?.let {
                val palette = Palette.from(it).generate()
                backgroundColor = Color(palette.getDominantColor(android.graphics.Color.DKGRAY))
            }
        }
    }

    if (showSpeedDialog) {
        PlaybackSpeedDialog(
            currentSpeed = playerState.playbackSpeed,
            onSpeedSelected = { viewModel.setSpeed(it); showSpeedDialog = false },
            onDismiss = { showSpeedDialog = false }
        )
    }

    if (showTimerDialog) {
        SleepTimerDialog(
            onTimerSelected = { viewModel.setSleepTimer(it); showTimerDialog = false },
            onDismiss = { showTimerDialog = false }
        )
    }

    if (showShareDialog) {
        val friends by viewModel.friendsList.collectAsState()
        ShareSongDialog(
            friends = friends,
            onFriendSelected = { friend ->
                viewModel.shareSongWithFriend(friend.id, song.id)
                showShareDialog = false
                Toast.makeText(context, "Song shared successfully!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showShareDialog = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(backgroundColor, Color.Black)))) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) { Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.White) }
                Text(stringResource(R.string.now_playing), color = Color.White, style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp)
                IconButton(onClick = { showShareDialog = true }) { Icon(Icons.Default.Share, null, tint = Color.White) }
            }
            Spacer(modifier = Modifier.weight(0.5f))
            RotatingDisk(song = song, isPlaying = playerState.isPlaying)
            Spacer(modifier = Modifier.weight(0.5f))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(song.title, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text("Melodify Stream", style = MaterialTheme.typography.bodyLarge, color = Color.LightGray)
                }
                IconButton(onClick = { viewModel.toggleLike(song.id, isLiked) }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            WaveformVisualizer(isPlaying = playerState.isPlaying, color = backgroundColor)
            Spacer(modifier = Modifier.height(32.dp))
            PlaybackProgressBar(playerState, viewModel::seekTo)
            Spacer(modifier = Modifier.height(24.dp))
            PlaybackControls(playerState, viewModel)
            Spacer(modifier = Modifier.weight(0.5f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                IconButton(onClick = { showSpeedDialog = true }) { Icon(Icons.Default.Speed, null, tint = Color.White) }
                IconButton(onClick = {
                    viewModel.downloadSong(song.id) { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Download", tint = Color.White)
                }
                IconButton(onClick = { showTimerDialog = true }) { Icon(Icons.Default.Timer, null, tint = Color.White) }
            }
        }
    }
}

@Composable
fun ShareSongDialog(friends: List<User>, onFriendSelected: (User) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Song with Friend") },
        text = {
            if (friends.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(text = "You are not following any users yet.", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(friends) { friend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onFriendSelected(friend) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = friend.profileImage.ifEmpty { "https://www.w3schools.com/howto/img_avatar.png" },
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = friend.fullName, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
fun PlaybackSpeedDialog(currentSpeed: Float, onSpeedSelected: (Float) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.playback_speed)) },
        text = {
            Column {
                listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { speed ->
                    Row(Modifier.fillMaxWidth().clickable { onSpeedSelected(speed) }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = currentSpeed == speed, onClick = { onSpeedSelected(speed) })
                        Text("${speed}x", Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SleepTimerDialog(onTimerSelected: (Int) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.sleep_timer)) },
        text = {
            Column {
                listOf(0, 15, 30, 60).forEach { mins ->
                    val label = if (mins == 0) "Off" else "$mins minutes"
                    Text(label, Modifier.fillMaxWidth().clickable { onTimerSelected(mins) }.padding(16.dp))
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun RotatingDisk(song: Song, isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "CD Rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(10000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "Rotation"
    )
    Box(modifier = Modifier.size(280.dp).graphicsLayer { rotationZ = if (isPlaying) rotation else 0f }.clip(CircleShape).background(Color.Black), contentAlignment = Alignment.Center) {
        AsyncImage(model = song.coverUrl, contentDescription = null, modifier = Modifier.fillMaxSize().padding(4.dp).clip(CircleShape), contentScale = ContentScale.Crop)
        Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color.Black.copy(alpha = 0.5f), border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.3f))) {}
    }
}

@Composable
fun WaveformVisualizer(isPlaying: Boolean, color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "Waveform")
    val waveHeights = List(15) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f, targetValue = if (isPlaying) 1f else 0.2f,
            animationSpec = infiniteRepeatable(animation = tween(400 + (index * 50), easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
            label = "Wave$index"
        )
    }
    Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
        val width = size.width
        val height = size.height
        val barWidth = width / (waveHeights.size * 2)
        waveHeights.forEachIndexed { index, animValue ->
            val x = index * barWidth * 2 + barWidth / 2
            val barHeight = height * animValue.value
            drawLine(color = color.copy(alpha = 0.7f), start = androidx.compose.ui.geometry.Offset(x, (height - barHeight) / 2), end = androidx.compose.ui.geometry.Offset(x, (height + barHeight) / 2), strokeWidth = barWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        }
    }
}

@Composable
fun PlaybackProgressBar(state: PlayerState, onSeek: (Long) -> Unit) {
    Column {
        Slider(value = if (state.duration > 0) state.currentPosition.toFloat() else 0f, onValueChange = { onSeek(it.toLong()) }, valueRange = 0f..(if (state.duration > 0) state.duration.toFloat() else 1f), colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White, inactiveTrackColor = Color.White.copy(alpha = 0.3f)))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatTime(state.currentPosition), color = Color.LightGray, fontSize = 12.sp)
            Text(formatTime(state.duration), color = Color.LightGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun PlaybackControls(state: PlayerState, viewModel: PlayerViewModel) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { viewModel.toggleShuffle() }) { Icon(Icons.Default.Shuffle, null, tint = if (state.shuffleEnabled) Color.Green else Color.White) }
        IconButton(onClick = { viewModel.previous() }) { Icon(Icons.Default.SkipPrevious, null, tint = Color.White, modifier = Modifier.size(40.dp)) }
        ElevatedButton(onClick = { viewModel.pauseResume() }, modifier = Modifier.size(72.dp), shape = CircleShape, contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.White)) {
            Icon(if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(40.dp))
        }
        IconButton(onClick = { viewModel.next() }) { Icon(Icons.Default.SkipNext, null, tint = Color.White, modifier = Modifier.size(40.dp)) }
        IconButton(onClick = { viewModel.setRepeatMode((state.repeatMode + 1) % 3) }) { Icon(Icons.Default.Repeat, null, tint = if (state.repeatMode > 0) Color.Green else Color.White) }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
