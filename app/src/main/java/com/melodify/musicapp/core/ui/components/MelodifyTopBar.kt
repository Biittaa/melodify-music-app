package com.melodify.musicapp.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.melodify.musicapp.core.ui.theme.MelodifyColors
import com.melodify.musicapp.core.ui.theme.Space8
import com.melodify.musicapp.R

@OptIn(ExperimentalMaterial3Api::class)  // ✅ این رو اضافه کن
@Composable
fun MelodifyTopBar(
    modifier: Modifier = Modifier,  // ✅ modifier باید اولین پارامتر باشه
    title: String,
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    showActions: Boolean = true,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Space8)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_melodify_logo),  // ✅ این رو کامل کن
                    contentDescription = stringResource(R.string.app_logo),
                    tint = MelodifyColors().primary,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        actions = {
            if (showActions) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.notifications)
                    )
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings)
                    )
                }
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.profile)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MelodifyColors().surface,
            titleContentColor = MelodifyColors().onSurface,
            actionIconContentColor = MelodifyColors().onSurface
        )
    )
}