package com.melodify.musicapp.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.melodify.musicapp.core.ui.theme.MelodifyColors
import com.melodify.musicapp.core.ui.theme.Space12
import com.melodify.musicapp.core.ui.theme.Space8
import com.melodify.musicapp.R

@Composable
fun MiniPlayer(
    currentSong: Song?,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentSong == null) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Space12, vertical = Space8)
            .clickable { onExpand() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MelodifyColors().surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Space12),
            horizontalArrangement = Arrangement.spacedBy(Space12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cover
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentSong.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = currentSong.title,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = currentSong.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MelodifyColors().onSurface,
                    maxLines = 1
                )
                Text(
                    text = currentSong.artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MelodifyColors().onSurface.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }

            // Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(Space8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) stringResource(R.string.pause) else stringResource(R.string.play),
                        tint = MelodifyColors().primary
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = stringResource(R.string.next),
                        tint = MelodifyColors().onSurface
                    )
                }
            }
        }
    }
}