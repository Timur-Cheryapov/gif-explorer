package com.byticher.gif.presentation.components.swipe

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

fun Modifier.swipable(
    state: SwipableCardState,
    onSwiped: () -> Unit = {}
): Modifier = this
    .pointerInput(Unit) {
        coroutineScope {
            detectDragGestures(
                onDragCancel = { launch { state.reset() } },
                onDrag = { _, dragAmount ->
                    launch {
                        state.onDrag(dragAmount)
                    }
                },
                onDragEnd = {
                    // Prevent "double" swipes
                    if (state.direction == null) {
                        if (hasTravelledEnough(state)) {
                            launch {
                                state.swipe()
                            }
                            launch {
                                // Delay can be added for better User Experience
                                //delay(90)
                                onSwiped()
                            }
                        } else {
                            launch { state.reset() }
                        }
                    }
                }
            )
        }
    }
    .graphicsLayer {
        translationX = state.offset.value.x
        translationY = state.offset.value.y
        rotationZ = state.offset.value.x * 0.03f
    }

private fun hasTravelledEnough(
    state: SwipableCardState
): Boolean {
    return abs(state.offset.value.x) > state.maxWidth / 4
}