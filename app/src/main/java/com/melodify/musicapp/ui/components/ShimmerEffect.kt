package com.melodify.musicapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.melodify.musicapp.ui.theme.MelodifyLightGray
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width

@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp)
) {
    var shimmerState by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        shimmerState = true
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(MelodifyLightGray)
    ) {
        if (shimmerState) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MelodifyLightGray.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.3f),
                                MelodifyLightGray.copy(alpha = 0.2f)
                            ),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    )
            )
        }
    }
}

@Composable
fun ShimmerText(
    modifier: Modifier = Modifier,
    width: Dp = 100.dp,
    height: Dp = 16.dp
) {
    ShimmerLoading(
        modifier = modifier
            .width(width)
            .height(height)
    )
}