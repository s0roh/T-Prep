package com.example.feature.profile.presentation.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage

@Composable
internal fun ProfileImage(imageUri: Uri?, onClick: () -> Unit) {
    AnimatedContent(
        targetState = imageUri,
        transitionSpec = {
            fadeIn(animationSpec = tween(1000)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(500)
            ) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "Profile Image Transition"
    ) { uri ->
        if (uri != null) {
            SubcomposeAsyncImage(
                model = uri,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { onClick() },
                loading = {
                    CircularProgressIndicator()
                },
            )
        } else {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Default Profile Icon",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onClick() }
                    .padding(1.dp)
            )
        }
    }
}