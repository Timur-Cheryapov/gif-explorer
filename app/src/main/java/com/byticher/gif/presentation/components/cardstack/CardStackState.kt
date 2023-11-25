package com.byticher.gif.presentation.components.cardstack

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.ui.geometry.Size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.byticher.gif.const.ConstValues.ITEM_AMOUNT_DISPLAYED
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

@Composable
fun rememberCardStackState(
    layoutSize: Size
): CardStackState {

    return remember {
        CardStackState(layoutSize)
    }
}

@Stable
class CardStackState(
    layoutSize: Size
) {
    private val range = 1..(ITEM_AMOUNT_DISPLAYED - 1)
    private val totalWidth = layoutSize.width
    private val totalHeight = layoutSize.height

    // Dimensions
    val size = mutableListOf(
        Size(
            width = totalWidth * 0.8f,
            height = totalHeight * 0.8f
        )
    )
    private val aspectRatio = size[0].height/size[0].width

    // Position
    val position = mutableListOf(
        Offset(
            x = (totalWidth - size[0].width)/2,
            y = 0f
        )
    )
    val zIndex = mutableListOf(0f)

    // Constants
    private val xDelta = size[0].width * 0.02f
    private val yDelta = size[0].height * 0.013f

    // Calculate dimensions and positions for all items
    private fun calculate() {
        for (i in range) {
            // Dimensions
            val width = size[i - 1].width - xDelta * 2
            size.add(
                Size(
                    width = width,
                    height = width * aspectRatio
                )
            )
            //Position
            position.add(
                Offset(
                    x = position[i - 1].x + xDelta,
                    y = position[i - 1].y + size[i - 1].height - size[i].height + yDelta
                )
            )
            zIndex.add(zIndex[i - 1] - 1)
        }
    }

    // Target values for scale and offset
    // i - target for i element (future i-1 element)
    private var scaleTarget = mutableListOf(
        // Size() acts as Scale
        Size(1f, 1f)
    )
    private var offsetTarget = mutableListOf(
        Offset(0f, 0f)
    )

    private fun calculateTargets() {
        for (i in range) {
            scaleTarget.add(
                // Scale. Scales from the center to the sides
                Size(
                    width = size[i-1].width / size[i].width, // ScaleX
                    height = size[i-1].height / size[i].height // ScaleY
                )
            )
            offsetTarget.add(
                Offset(
                    // Compensates with scaleX
                    x = 0f,
                    // Compensates with scaleY
                    y = - yDelta - ((scaleTarget[i].height - 1) * size[i].height / 2)
                )
            )
        }
    }

    // Animated values for scale and offset
    var scaleAnimated = MutableList(ITEM_AMOUNT_DISPLAYED) {
        Animatable(
            // Size() acts as Scale
            Size(1f, 1f), Size.VectorConverter
        )
    }
    var offsetAnimated = MutableList(ITEM_AMOUNT_DISPLAYED) {
        Animatable(
            Offset(0f, 0f), Offset.VectorConverter
        )
    }
    // Animated alpha value for the last item in the stack
    var alphaForLastAnimated = Animatable(0f)

    init {
        calculate()
        calculateTargets()
    }

    suspend fun animateCards() = coroutineScope {
        launch { alphaForLastAnimated.animateTo(1f,
            tween(800, easing = EaseIn)) }
        for (i in range) {
            launch { offsetAnimated[i].animateTo(offsetTarget[i],
                tween(200, easing = EaseIn)
            ) }
            launch { scaleAnimated[i].animateTo(scaleTarget[i],
                tween(200, easing = EaseIn)
            ) }
            delay(30 * (i.pow(1.5f)).toLong())
        }
    }

    suspend fun snapBackCards() = coroutineScope {
        launch { alphaForLastAnimated.snapTo(0f) }
        for (i in range) {
            launch { offsetAnimated[i].snapTo(Offset(0f, 0f)) }
            launch { scaleAnimated[i].snapTo(Size(1f, 1f)) }
        }
    }
}

fun Int.pow(to: Float): Float {
    return this.toFloat().pow(to)
}