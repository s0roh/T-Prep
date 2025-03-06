package com.example.feature.profile.presentation.util

import android.net.Uri
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.toArgb
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.feature.profile.presentation.components.CropImageContractOptions

internal fun launchCrop(
    uri: Uri,
    cropImageLauncher: (CropImageContractOptions) -> Unit,
    themeColors: ColorScheme,
) {
    cropImageLauncher(
        CropImageContractOptions(
            uri,
            CropImageOptions(
                cropShape = CropImageView.CropShape.OVAL,
                fixAspectRatio = true,
                multiTouchEnabled = true,
                activityBackgroundColor = themeColors.background.toArgb(),
                toolbarColor = themeColors.background.toArgb(),
                toolbarBackButtonColor = themeColors.onBackground.toArgb(),
                activityMenuIconColor = themeColors.onBackground.toArgb(),
                activityMenuTextColor = themeColors.secondary.toArgb()
            )
        )
    )
}