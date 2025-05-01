package com.example.common.util

import android.view.MotionEvent
import androidx.compose.runtime.Composable
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.rememberBalloonBuilder

@Composable
internal fun rememberConfiguredBalloonBuilder(
    backgroundColor: Int,
    overlayColor: Int,
    canDismissProvider: () -> Boolean,
    onDismissRequest: () -> Unit
): Balloon.Builder {
    return rememberBalloonBuilder {
        setArrowSize(7)
        setArrowPosition(0.5f)
        setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setArrowOrientation(ArrowOrientation.END)
        setMarginRight(60)
        setCornerRadius(8f)
        setBackgroundColor(backgroundColor)
        setBalloonAnimation(BalloonAnimation.OVERSHOOT)
        setIsVisibleOverlay(true)
        setOverlayColor(overlayColor)

        setDismissWhenOverlayClicked(false)
        setDismissWhenTouchOutside(false)
        setDismissWhenClicked(true)

        setOnBalloonOutsideTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_OUTSIDE -> {
                    if (canDismissProvider()) {
                        onDismissRequest()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    onDismissRequest()
                }
            }
        }
    }
}
