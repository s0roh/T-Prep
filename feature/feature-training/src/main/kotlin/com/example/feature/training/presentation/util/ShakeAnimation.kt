package com.example.feature.training.presentation.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween

internal suspend fun launchShakeAnimation(shakeOffset: Animatable<Float, AnimationVector1D>) {
    repeat(3) {
        shakeOffset.animateTo(5f, animationSpec = tween(25))
        shakeOffset.animateTo(-5f, animationSpec = tween(25))
    }
    shakeOffset.animateTo(0f, animationSpec = tween(25))
}