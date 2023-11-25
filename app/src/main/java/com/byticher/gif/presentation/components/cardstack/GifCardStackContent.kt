package com.byticher.gif.presentation.components.cardstack

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import kotlin.math.roundToInt

@Composable
fun GifCardStackContent(
    state: CardStackState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val size = state.size
    val position = state.position
    val zIndex = state.zIndex

    Layout(
        content = content,
        modifier = modifier
            .fillMaxSize()
    ) {measurables, constraints ->
        val totalWidth = constraints.maxWidth
        val totalHeight = constraints.maxHeight

        val placeables = measurables.mapIndexed { i, measurable ->
            measurable.measure(constraints.copy(
                minWidth = size[i].width.roundToInt(),
                maxWidth = size[i].width.roundToInt(),
                minHeight = size[i].height.roundToInt(),
                maxHeight = size[i].height.roundToInt()
            ))
        }

        layout(totalWidth, totalHeight) {
            if (placeables.isNotEmpty()) {
                for (i in placeables.indices) {
                    placeables[i].place(
                        x = position[i].x.roundToInt(),
                        y = position[i].y.roundToInt(),
                        zIndex = zIndex[i]
                    )
                }
            }
        }
    }
}