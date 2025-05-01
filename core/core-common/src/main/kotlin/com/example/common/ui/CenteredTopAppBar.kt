package com.example.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.common.R
import com.example.common.util.rememberConfiguredBalloonBuilder
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonWindow
import kotlinx.coroutines.delay

enum class NavigationIconType {
    NONE, BACK, CLOSE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredTopAppBar(
    title: String? = null,
    subtitle: String? = null,
    navigationIconType: NavigationIconType = NavigationIconType.NONE,
    showActions: Boolean? = null,
    containerColor: Color? = null,
    onNavigationClick: (() -> Unit)? = null,
    onRenameDeck: (() -> Unit)? = null,
    onChangePrivacy: (() -> Unit)? = null,
    onOwner: (() -> Unit)? = null,
    onDeckStatistic: (() -> Unit)? = null,
    onTrainingSettings: (() -> Unit)? = null,
    onDeleteDeck: (() -> Unit)? = null,
    isPublic: Boolean? = null,
    shouldShowTooltip: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    var canDismiss by remember { mutableStateOf(false) }
    var balloonWindow by rememberBalloonWindow(null)
    var isBalloonShown by rememberSaveable { mutableStateOf(false) }

    val overlayColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f).toArgb()
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.toArgb()

    LaunchedEffect(Unit) {
        if (shouldShowTooltip) {
            delay(2000L)
            canDismiss = true
        }
    }

    val builder = rememberConfiguredBalloonBuilder(
        backgroundColor = backgroundColor,
        overlayColor = overlayColor,
        canDismissProvider = { canDismiss },
        onDismissRequest = { balloonWindow?.dismiss() }
    )

    CenterAlignedTopAppBar(
        colors = containerColor?.let {
            TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = containerColor)
        } ?: TopAppBarDefaults.centerAlignedTopAppBarColors(),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (title != null) {
                    Text(
                        text = title,
                        style = if (subtitle != null) MaterialTheme.typography.titleLarge
                        else MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis

                    )
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            if (navigationIconType != NavigationIconType.NONE && onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        painter = painterResource(
                            when (navigationIconType) {
                                NavigationIconType.BACK -> R.drawable.ic_arrow_back
                                NavigationIconType.CLOSE -> R.drawable.ic_close
                                NavigationIconType.NONE -> 0
                            }
                        ),
                        contentDescription = "Navigation Icon"
                    )
                }
            }
        },
        actions = {
            if (onRenameDeck != null || onChangePrivacy != null || onTrainingSettings != null || onDeleteDeck != null) {
                Balloon(
                    builder = builder,
                    onBalloonWindowInitialized = { balloonWindow = it },
                    onComposedAnchor = {
                        if (shouldShowTooltip && !isBalloonShown) {
                            balloonWindow?.showAlignEnd()
                            isBalloonShown = true
                        }
                    },
                    balloonContent = {
                        Text(
                            text = if (showActions == true) {
                                stringResource(R.string.local_text_balloon)
                            } else {
                                stringResource(R.string.public_text_balloon)
                            },
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(
                                start = 6.dp,
                                end = 10.dp,
                                top = 6.dp,
                                bottom = 6.dp
                            )
                        )
                    }
                ) {
                    IconButton(onClick = {
                        if (isBalloonShown) {
                            balloonWindow?.dismiss()
                            isBalloonShown = false
                        }
                        expanded = true
                    }) {
                        Icon(
                            painterResource(R.drawable.ic_menu),
                            contentDescription = "Actions Icon"
                        )
                    }
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    if (showActions == true) {
                        onRenameDeck?.let {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.edit_name)) },
                                onClick = {
                                    expanded = false
                                    it()
                                },
                                leadingIcon = {
                                    Icon(
                                        painterResource(R.drawable.ic_edit),
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                    if (showActions == true) {
                        onChangePrivacy?.let {
                            DropdownMenuItem(
                                text = {
                                    isPublic?.let {
                                        Text(
                                            if (isPublic) stringResource(R.string.make_private)
                                            else stringResource(R.string.make_public)
                                        )
                                    }
                                },
                                onClick = {
                                    expanded = false
                                    it()
                                },
                                leadingIcon = {
                                    Icon(
                                        painterResource(R.drawable.ic_outbox),
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                    if (showActions != true) {
                        onOwner?.let {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.go_to_owner)) },
                                onClick = {
                                    expanded = false
                                    it()
                                },
                                leadingIcon = {
                                    Icon(
                                        painterResource(R.drawable.ic_profile),
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                    onDeckStatistic?.let {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.statistic)) },
                            onClick = {
                                expanded = false
                                it()
                            },
                            leadingIcon = {
                                Icon(
                                    painterResource(R.drawable.ic_statistic),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                    onTrainingSettings?.let {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.train_settings)) },
                            onClick = {
                                expanded = false
                                it()
                            },
                            leadingIcon = {
                                Icon(
                                    painterResource(R.drawable.ic_settings),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                    if (showActions == true) {
                        onDeleteDeck?.let {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.delete)) },
                                onClick = {
                                    expanded = false
                                    it()
                                },
                                leadingIcon = {
                                    Icon(
                                        painterResource(R.drawable.ic_trash),
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}