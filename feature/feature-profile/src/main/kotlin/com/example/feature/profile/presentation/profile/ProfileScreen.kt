package com.example.feature.profile.presentation.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.feature.profile.presentation.components.CropImageContract
import com.example.feature.profile.presentation.components.ProfileHeader
import com.example.feature.profile.presentation.components.StatisticsSection
import com.example.feature.profile.presentation.util.launchCrop

@Composable
fun ProfileScreen(
    paddingValues: PaddingValues,
    onLogoutClick: () -> Unit,
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val navController = rememberNavController()
    val context = LocalContext.current
    var showDialog = remember { mutableStateOf(false) }
    val themeColors = MaterialTheme.colorScheme
    val screenState by viewModel.screenState.collectAsState()

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.refreshProfile()
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

    if (showDialog.value) {
        ImageSourceDialog(
            onDismiss = { showDialog.value = false },
            onGalleryClick = { pickImageLauncher.launch("image/*") },
            onCameraClick = { captureImageLauncher.launch(null) }
        )
    }

    when (val currentState = screenState) {
        ProfileScreenState.Error -> {}
        ProfileScreenState.Loading -> {
            CircularProgressIndicator()
        }

        is ProfileScreenState.Success -> {
            ProfileScreenContent(
                paddingValues = paddingValues,
                currentState = currentState,
                showDialog = showDialog,
                onLogoutClick = {
                    viewModel.logout()
                    onLogoutClick()
                }
            )
        }
    }
}

@Composable
private fun ProfileScreenContent(
    paddingValues: PaddingValues,
    currentState: ProfileScreenState.Success,
    showDialog: MutableState<Boolean>,
    onLogoutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 25.dp, vertical = 20.dp)
            .padding(paddingValues)
            .fillMaxSize(),
    ) {

        ProfileHeader(currentState, showDialog)

        Spacer(modifier = Modifier.height(41.dp))

        StatisticsSection(currentState)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            onClick = { onLogoutClick() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Logout",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Выйти")
        }
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
        title = { Text("Выберите источник изображения") },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onGalleryClick()
            }) {
                Text("Галерея")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                onCameraClick()
            }) {
                Text("Камера")
            }
        }
    )
}