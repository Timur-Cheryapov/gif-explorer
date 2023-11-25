package com.byticher.gif.presentation.components.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun rememberSwipableCardState(): SwipableCardState {
    val screenWidth = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    val screenHeight = with(LocalDensity.current) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }
    return remember {
        SwipableCardState(screenWidth, screenHeight)
    }
}

enum class Direction { LEFT, RIGHT }

@Stable
class SwipableCardState(
    val maxWidth: Float,
    val maxHeight: Float
) {
    val offset = Animatable(Offset(0f, 0f), Offset.VectorConverter)
    var direction: Direction? = null

    suspend fun onDrag(change: Offset = Offset(0f, 0f)) {
        val offsetX = offset.value.x + change.x
        // If is for preventing swipes before the new item is shown
        if (direction == null) {
            offset.snapTo(
                Offset(
                    x = offsetX,
                    y = -abs(offsetX * 0.6f)
                )
            )
        }
    }

    suspend fun swipe() {
        if (offset.value.x > 0) {
            direction = Direction.RIGHT
            offset.animateTo(
                Offset(
                    x = maxWidth * 1.5f,
                    y = -maxHeight * 0.4f
                )
            )
        } else {
            direction = Direction.LEFT
            offset.animateTo(
                Offset(
                    x = -maxWidth * 1.5f,
                    y = -maxHeight * 0.4f
                )
            )
        }
    }

    suspend fun reset() {
        offset.animateTo(
            Offset(
                x = 0f,
                y = 0f
            )
        )
    }

    suspend fun snapBack(){
        offset.snapTo(
            Offset(
                x = 0f,
                y = 0f
            )
        )
        direction = null
    }
}