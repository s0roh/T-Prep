package com.example.feature.decks.presentation.util

import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.overlay.BalloonOverlayRoundRect

@Composable
internal fun rememberPublicDecksBalloonBuilder(
    backgroundColor: Int,
    overlayColor: Int,
    canDismissProvider: () -> Boolean = { true },
    onDismissRequest: () -> Unit = {}
): Balloon.Builder {
    val density = LocalDensity.current
    val cornerRadiusPx = with(density) { 16.dp.toPx() }

    return rememberBalloonBuilder {
        setArrowSize(7)
        setArrowPosition(0.5f)
        setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
        setArrowOrientation(ArrowOrientation.TOP)
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setMarginTop(6)
        setMarginHorizontal(24)
        setCornerRadius(8f)
        setBackgroundColor(backgroundColor)
        setBalloonAnimation(BalloonAnimation.OVERSHOOT)
        setIsVisibleOverlay(true)
        setOverlayColor(overlayColor)
        setOverlayShape(BalloonOverlayRoundRect(cornerRadiusPx, cornerRadiusPx))

        setShouldPassTouchEventToAnchor(true)

        setDismissWhenOverlayClicked(false)
        setDismissWhenTouchOutside(false)
        setDismissWhenClicked(true)

        setOnBalloonOutsideTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE && canDismissProvider()) {
                onDismissRequest()
            }
        }
    }
}
