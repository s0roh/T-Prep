package com.example.common.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.common.R
import com.example.common.ui.entity.DeckUiModel
import com.example.common.util.formatCount
import com.example.common.util.getCardWordForm

@Composable
fun DeckCard(
    modifier: Modifier = Modifier,
    deck: DeckUiModel,
    onDeckClickListener: (String) -> Unit,
    onLikeClickListener: ((String, Boolean) -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { onDeckClickListener(deck.id) },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            DeckInfo(deck)
            Spacer(modifier = Modifier.height(20.dp))
            DeckFooter(deck, onLikeClickListener)
        }
    }
}

@Composable
private fun DeckInfo(deck: DeckUiModel) {
    Text(
        text = deck.name,
        style = MaterialTheme.typography.titleLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    Text(
        text = "${deck.cardsCount} ${getCardWordForm(deck.cardsCount)}",
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
private fun DeckFooter(
    deck: DeckUiModel,
    onLikeClickListener: ((String, Boolean) -> Unit)?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DeckVisibility(deck)
        onLikeClickListener?.let {
            DeckStats(deck, onLikeClickListener)
        }
    }
}

@Composable
private fun DeckVisibility(deck: DeckUiModel) {
    Row {
        Icon(
            painter = painterResource(
                if (deck.isPublic) R.drawable.ic_public_card
                else R.drawable.ic_private_card
            ),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (deck.isPublic) "Публичная" else "Приватная",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

@Composable
private fun DeckStats(
    deck: DeckUiModel,
    onLikeClickListener: (String, Boolean) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatCount(deck.trainings),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Icon(
            painter = painterResource(R.drawable.ic_training),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LikeButtonWithAnimation(
            likeCount = deck.likes,
            isLiked = deck.isLiked,
            onClick = { onLikeClickListener(deck.id, deck.isLiked) }
        )
    }
}

@Composable
private fun LikeButtonWithAnimation(
    likeCount: Int,
    isLiked: Boolean,
    onClick: () -> Unit,
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isLiked) 1.01f else 1f,
        animationSpec = keyframes {
            durationMillis = 600
            1f at 0 using FastOutSlowInEasing
            1.3f at 150 using FastOutSlowInEasing
            1f at 300 using FastOutSlowInEasing
            1.1f at 450 using FastOutSlowInEasing
            1f at 600 using FastOutSlowInEasing
        }
    )

    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        AnimatedLikeCount(likeCount, isLiked)
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            tint = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
            modifier = Modifier.scale(animatedScale)
        )
    }
}

@Composable
private fun AnimatedLikeCount(
    likeCount: Int,
    isLiked: Boolean,
) {
    AnimatedContent(
        targetState = likeCount,
        label = "LikeCountTransition"
    ) { count ->
        if (count != 0) {
            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = formatCount(count),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}