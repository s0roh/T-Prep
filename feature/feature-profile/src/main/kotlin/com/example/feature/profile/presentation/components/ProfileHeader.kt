package com.example.feature.profile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.feature.profile.presentation.profile.ProfileScreenState

@Composable
internal fun ProfileHeader(
    state: ProfileScreenState.Success,
    onDeleteProfileImage: () -> Unit,
    onChangeImageClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ProfileImage(
            imageUri = state.profileImageUri,
            onClick = onChangeImageClick,
            onDelete = onDeleteProfileImage
        )
        Spacer(modifier = Modifier.width(35.dp))
        Column {
            Text(
                text = state.userName,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = state.userEmail,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}