package com.melodify.musicapp.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.melodify.musicapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Theme
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.dark_mode))
                Switch(
                    checked = settings.darkMode,
                    onCheckedChange = { viewModel.toggleDarkMode(it) }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Language
            Column {
                Text(text = stringResource(R.string.language), style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = settings.language == "fa",
                        onClick = { viewModel.setLanguage("fa") }
                    )
                    Text((stringResource(R.string.persian)), modifier = Modifier.padding(start = 8.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = settings.language == "en",
                        onClick = { viewModel.setLanguage("en") }
                    )
                    Text((stringResource(R.string.english)), modifier = Modifier.padding(start = 8.dp))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Font Scale
            Column {
                Text((stringResource(R.string.font_size)), style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = settings.fontScale,
                    onValueChange = { viewModel.setFontScale(it) },
                    valueRange = 0.8f..1.5f,
                    steps = 7
                )
//                Text(text = "مقدار: ${settings.fontScale}", style = MaterialTheme.typography.bodySmall)
                Text(
                        stringResource(
                            R.string.font_scale,
                            settings.fontScale
                        )
                    )
            }


            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Notifications
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Text(text = "اعلان‌ها")
                Text(stringResource(R.string.notifications))
                Switch(
                    checked = settings.notificationEnabled,
                    onCheckedChange = { viewModel.setNotificationEnabled(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Info
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Text(
                    text = "Melodify v1.0.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}