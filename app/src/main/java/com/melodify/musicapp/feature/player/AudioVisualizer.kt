package com.melodify.musicapp.feature.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius  // ✅ این خط رو اضافه کن
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun AudioVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val barCount = 30
    val amplitudes = remember { mutableStateListOf<Float>().apply { repeat(barCount) { add(0f) } } }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(50)
            amplitudes.indices.forEach { i ->
                val baseAmplitude = (0.3f + 0.7f * sin(i * 0.5f + System.currentTimeMillis() * 0.005f))
                val randomFactor = 0.5f + 0.5f * sin(System.currentTimeMillis() * 0.01f + i * 0.3f)
                amplitudes[i] = baseAmplitude * randomFactor * 0.8f
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        val barWidth = size.width / barCount * 0.7f
        val spacing = size.width / barCount * 0.3f
        val maxHeight = size.height * 0.8f

        amplitudes.forEachIndexed { index, amplitude ->
            val x = index * (barWidth + spacing) + spacing / 2
            val height = maxHeight * amplitude
            val y = (size.height - height) / 2

            drawRoundRect(
                color = Color.White.copy(alpha = 0.3f + 0.7f * amplitude),
                topLeft = Offset(x, y),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(barWidth / 2)  // ✅ الان کار میکنه
            )
        }
    }
}