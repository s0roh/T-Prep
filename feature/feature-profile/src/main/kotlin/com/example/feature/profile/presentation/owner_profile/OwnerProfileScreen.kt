package com.example.feature.profile.presentation.owner_profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.SubcomposeAsyncImage
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.DeckCard
import com.example.common.ui.LoadingState
import com.example.common.ui.NavigationIconType
import com.example.feature.profile.R
import com.example.feature.profile.presentation.components.StatisticsSection

@Composable
fun OwnerProfileScreen(
    ownerId: String,
    onBackClick: () -> Unit,
    onDeckClickListener: (String) -> Unit,
    onTrainClick: (String) -> Unit,
    onScheduleClick: (String, String) -> Unit,
) {
    val viewModel: OwnerProfileViewModel = hiltViewModel()
    val screenState by viewModel.screenState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(ownerId) {
        viewModel.loadOwnerProfile(ownerId = ownerId)
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is OwnerProfileEvent.ShowError -> {
                    Toast.makeText(context, "Ошибка: ${event.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    when (val currentState = screenState) {
        OwnerProfileScreenState.Loading -> {
            LoadingState()
        }

        is OwnerProfileScreenState.Success -> {
            OwnerProfileContent(
                state = currentState,
                onBackClick = onBackClick,
                onDeckClickListener = onDeckClickListener,
                onTrainClick = onTrainClick,
                onScheduleClick = onScheduleClick,
                onLikeClickListener = viewModel::onLikeClick
            )
        }
    }
}

@Composable
private fun OwnerProfileContent(
    state: OwnerProfileScreenState.Success,
    onBackClick: () -> Unit,
    onDeckClickListener: (String) -> Unit,
    onTrainClick: (String) -> Unit,
    onScheduleClick: (String, String) -> Unit,
    onLikeClickListener: (String, Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = stringResource(R.string.deck_owner),
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(horizontal = 25.dp)
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            OwnerProfileHeader(state = state)

            Spacer(modifier = Modifier.height(41.dp))

            StatisticsSection(state)

            Spacer(modifier = Modifier.height(31.dp))


            Text(
                text = stringResource(R.string.pubic_decks),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                }
                items(items = state.ownerPublicDecks) { deck ->
                    DeckCard(
                        deck = deck,
                        onDeckClickListener = onDeckClickListener,
                        onTrainClick = onTrainClick,
                        onScheduleClick = { onScheduleClick(deck.id, deck.name) },
                        onLikeClickListener = onLikeClickListener,
                        modifier = Modifier.animateItem()
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun OwnerProfileHeader(
    state: OwnerProfileScreenState.Success,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (state.profileImageUri != null) {
            SubcomposeAsyncImage(
                model = state.profileImageUri,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                loading = {
                    CircularProgressIndicator()
                },
            )
        } else {
            Image(
                painter = painterResource(R.drawable.profile_image),
                contentDescription = "Default Profile Icon",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        Spacer(modifier = Modifier.width(35.dp))

        Text(
            text = state.userName,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}