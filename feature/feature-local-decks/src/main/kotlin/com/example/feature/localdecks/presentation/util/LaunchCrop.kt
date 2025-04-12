package com.example.feature.localdecks.presentation.util

import android.net.Uri
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.toArgb
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

internal fun launchCrop(
    uri: Uri,
    cropImageLauncher: (CropImageContractOptions) -> Unit,
    themeColors: ColorScheme,
) {
    cropImageLauncher(
        CropImageContractOptions(
            uri,
            CropImageOptions(
                cropShape = CropImageView.CropShape.RECTANGLE,
                aspectRatioX = 16,
                aspectRatioY = 9,
                fixAspectRatio = true,
                activityBackgroundColor = themeColors.background.toArgb(),
                toolbarColor = themeColors.background.toArgb(),
                toolbarBackButtonColor = themeColors.onBackground.toArgb(),
                activityMenuIconColor = themeColors.onBackground.toArgb(),
                activityMenuTextColor = themeColors.secondary.toArgb()
            )
        )
    )
}