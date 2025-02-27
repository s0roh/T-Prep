package com.example.common.ui

import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.common.R

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
    onTrainingSettings: (() -> Unit)? = null,
    onDeleteDeck: (() -> Unit)? = null,
    isPublic: Boolean? = null
) {
    var expanded by remember { mutableStateOf(false) }

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
                IconButton(onClick = { expanded = true }) {
                    Icon(painterResource(R.drawable.ic_menu), contentDescription = "Actions Icon")
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    if (showActions == true) {
                        onRenameDeck?.let {
                            DropdownMenuItem(
                                text = { Text("Изменить название") },
                                onClick = {
                                    expanded = false
                                    it()
                                },
                                leadingIcon = {
                                    Icon(painterResource(R.drawable.ic_edit), contentDescription = null)
                                }
                            )
                        }
                    }
                    if (showActions == true) {
                        onChangePrivacy?.let {
                            DropdownMenuItem(
                                text = {
                                    isPublic?.let {
                                        Text(if (isPublic) "Сделать приватной" else "Сделать публичной")
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
                    onTrainingSettings?.let {
                        DropdownMenuItem(
                            text = { Text("Настройки тренировки") },
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
                                text = { Text("Удалить") },
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