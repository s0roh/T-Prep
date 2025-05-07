package com.example.feature.profile.presentation.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.AppElevatedButton
import com.example.common.ui.LoadingState
import com.example.feature.profile.R
import com.example.feature.profile.presentation.components.CropImageContract
import com.example.feature.profile.presentation.components.ProfileHeader
import com.example.feature.profile.presentation.components.StatisticsSection
import com.example.feature.profile.presentation.util.launchCrop

@Composable
fun ProfileScreen(
    paddingValues: PaddingValues,
    onSettingsClick: () -> Unit,
    onFavouriteDecksClick: () -> Unit,
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val themeColors = MaterialTheme.colorScheme
    val screenState by viewModel.screenState.collectAsState()

    LaunchedEffect(screenState) {
        when (screenState) {
            is ProfileScreenState.Error -> {
                val message = (screenState as ProfileScreenState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                viewModel.refreshProfile()
            }

            else -> Unit
        }
    }

    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.setProfileImage(result.uriContent.toString())
        }
    }

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { launchCrop(uri, cropImageLauncher::launch, themeColors) }
        }

    val captureImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                val uri = viewModel.bitmapToUri(context, it)
                launchCrop(uri, cropImageLauncher::launch, themeColors)
            }
        }

    if (showDialog) {
        ImageSourceDialog(
            onDismiss = { showDialog = false },
            onGalleryClick = { pickImageLauncher.launch("image/*") },
            onCameraClick = { captureImageLauncher.launch(null) }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(paddingValues),
                onClick = onSettingsClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            }
        }
    ) { innerPadding ->
        when (val currentState = screenState) {
            ProfileScreenState.Loading -> {
                LoadingState()
            }

            is ProfileScreenState.Success -> {
                ProfileScreenContent(
                    paddingValues = innerPadding,
                    state = currentState,
                    onDeleteProfileImage = { viewModel.deleteProfileImage() },
                    onChangeImageClick = { showDialog = true },
                    onFavouriteDecksClick = {
                        viewModel.onFavouriteDecksClick()
                        onFavouriteDecksClick()
                    }
                )
            }

            else -> Unit
        }
    }
}

@Composable
private fun ProfileScreenContent(
    paddingValues: PaddingValues,
    state: ProfileScreenState.Success,
    onDeleteProfileImage: () -> Unit,
    onChangeImageClick: () -> Unit,
    onFavouriteDecksClick: () -> Unit,
) {
    val alreadyClicked = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(horizontal = 25.dp, vertical = 20.dp)
            .padding(paddingValues)
            .fillMaxSize(),
    ) {

        ProfileHeader(
            state = state,
            onChangeImageClick = onChangeImageClick,
            onDeleteProfileImage = onDeleteProfileImage
        )

        Spacer(modifier = Modifier.height(41.dp))

        StatisticsSection(state = state)

        Spacer(modifier = Modifier.height(41.dp))

        AppElevatedButton(
            title = stringResource(R.string.open_favourite_decks),
            shouldShowIcon = true,
            iconResId = R.drawable.ic_heart,
            onClick = {
                if (!alreadyClicked.value) {
                    alreadyClicked.value = true
                    onFavouriteDecksClick()
                }
            },
            enabled = !alreadyClicked.value,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_image_source)) },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onGalleryClick()
            }) {
                Text(stringResource(R.string.gallery))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                onCameraClick()
            }) {
                Text(stringResource(R.string.camera))
            }
        }
    )
}