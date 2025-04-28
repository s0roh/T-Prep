package com.example.feature.profile.presentation.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.example.feature.profile.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ProfileImage(
    imageUri: Uri?,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }
    AnimatedContent(
        targetState = imageUri,
        transitionSpec = {
            fadeIn(animationSpec = tween(1000)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(500)
            ) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = stringResource(R.string.profile_image_transition)
    ) { uri ->
        if (uri != null) {
            SubcomposeAsyncImage(
                model = uri,
                contentDescription = stringResource(R.string.profile_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .combinedClickable(
                        onClick = { onClick() },
                        onLongClick = { expanded.value = true }
                    ),
                loading = {
                    CircularProgressIndicator()
                },
            )
        } else {
            Image(
              painter = painterResource(R.drawable.profile_image),
                contentDescription = stringResource(R.string.default_profile_icon),
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onClick() }
            )
        }
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
    ) {
        DropdownMenuItem(
            onClick = {
                expanded.value = false
                onDelete()
            },
            text = {
                Text(stringResource(R.string.delete))
            }
        )
    }
}