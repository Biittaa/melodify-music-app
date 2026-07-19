package com.melodify.musicapp.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.melodify.musicapp.R
import com.melodify.musicapp.ui.components.MelodifyTopBar
import com.melodify.musicapp.ui.theme.MelodifyColors
import com.melodify.musicapp.ui.theme.Space16

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MelodifyTopBar(
                title = stringResource(R.string.settings),
                showActions = false,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = MelodifyColors().background
    ) { paddingValues ->
        SettingsContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MelodifyColors().background),
        contentPadding = PaddingValues(Space16),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Theme
        item {
            SettingsCard(
                icon = Icons.Default.BrightnessMedium,
                title = stringResource(R.string.dark_mode)
            ) {
                Switch(
                    checked = uiState.isDarkMode,
                    onCheckedChange = { onEvent(SettingsEvent.OnToggleDarkMode) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MelodifyColors().primary,
                        checkedTrackColor = MelodifyColors().primary.copy(alpha = 0.3f)
                    )
                )
            }
        }

        // Language
        item {
            SettingsCard(
                icon = Icons.Default.Language,
                title = stringResource(R.string.language),
                subtitle = when (uiState.language) {
                    "en" -> "English"
                    "fa" -> "فارسی"
                    else -> "English"
                }
            ) {
                IconButton(
                    onClick = { onEvent(SettingsEvent.OnShowLanguageDialog) }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MelodifyColors().onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Font Size
        item {
            SettingsCard(
                icon = Icons.Default.TextFields,
                title = stringResource(R.string.font_size)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onEvent(SettingsEvent.OnDecreaseFontSize) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null,
                            tint = MelodifyColors().primary
                        )
                    }

                    Text(
                        text = "${(uiState.fontScale * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MelodifyColors().onSurface
                    )

                    IconButton(
                        onClick = { onEvent(SettingsEvent.OnIncreaseFontSize) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MelodifyColors().primary
                        )
                    }
                }
            }
        }

        // Notifications
        item {
            SettingsCard(
                icon = Icons.Default.Notifications,
                title = stringResource(R.string.notifications)
            ) {
                Switch(
                    checked = uiState.isNotificationEnabled,
                    onCheckedChange = { onEvent(SettingsEvent.OnToggleNotifications) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MelodifyColors().primary,
                        checkedTrackColor = MelodifyColors().primary.copy(alpha = 0.3f)
                    )
                )
            }
        }

        // About
        item {
            SettingsCard(
                icon = Icons.Default.Info,
                title = "About",
                subtitle = "Version 1.0.0"
            ) { }
        }

        // Language Dialog
        if (uiState.showLanguageDialog) {
            LanguageDialog(
                currentLanguage = uiState.language,
                onDismiss = { onEvent(SettingsEvent.OnDismissLanguageDialog) },
                onSelectLanguage = { lang -> onEvent(SettingsEvent.OnChangeLanguage(lang)) }
            )
        }
    }
}

@Composable
fun SettingsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MelodifyColors().surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Space16),
            horizontalArrangement = Arrangement.spacedBy(Space16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MelodifyColors().primary
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MelodifyColors().onSurface
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MelodifyColors().onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            content()
        }
    }
}

@Composable
fun LanguageDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSelectLanguage: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.language)) },
        text = {
            Column {
                listOf(
                    "en" to "English",
                    "fa" to "فارسی"
                ).forEach { (code, name) ->
                    TextButton(
                        onClick = {
                            onSelectLanguage(code)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(name)
                            if (code == currentLanguage) {
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