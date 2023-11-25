package com.byticher.gif.presentation.components.cardstack

import android.os.Vibrator
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.byticher.gif.const.ConstValues.ITEM_AMOUNT_DISPLAYED
import com.byticher.gif.domain.Gif
import com.byticher.gif.presentation.components.GifCard
import com.byticher.gif.domain.clickEasy
import com.byticher.gif.presentation.components.swipe.SwipableCardState
import com.byticher.gif.presentation.components.swipe.swipable
import com.byticher.gif.const.TestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Composable
fun GifCardStack(
    visibleGifs: ImmutableList<Gif>,
    swipableState: SwipableCardState,
    layoutSize: Size,
    modifier: Modifier = Modifier,
    beforeAnimation: () -> Unit,
    onItemSwiped: () -> Unit = {}
) {
    val stackState = rememberCardStackState(layoutSize = layoutSize)
    val animationScope = rememberCoroutineScope()
    val context = LocalContext.current
    val vibrator = remember {
        context.getSystemService(Vibrator::class.java)
    }

    GifCardStackContent(
        state = stackState,
        modifier = modifier.testTag(TestTags.GIF_CARD_STACK)
    ) {
        visibleGifs.forEachIndexed { index, gif ->
            // Thanks to the key() there is no flickering
            // and restarts of gifs after swipes :)
            // Exactly as I wished
            key(gif.id) {
                Box(modifier = if (index == 0) {
                    Modifier
                        .swipable(
                            state = swipableState,
                            onSwiped = {
                                animationScope.launch {
                                    beforeAnimation()
                                    vibrator.clickEasy()
                                    stackState.animateCards()
                                    onItemSwiped()
                                    swipableState.snapBack()
                                    stackState.snapBackCards()
                                }
                            }
                        )
                        .testTag(TestTags.SWIPABLE)
                } else {
                    Modifier
                        .graphicsLayer {
                            // Scales from the center to the sides
                            scaleX = stackState.scaleAnimated[index].value.width
                            scaleY = stackState.scaleAnimated[index].value.height

                            // Offset
                            // Basically x is always 0
                            translationX = stackState.offsetAnimated[index].value.x
                            translationY = stackState.offsetAnimated[index].value.y

                            // Alpha value for the last item in the stack
                            if (index == ITEM_AMOUNT_DISPLAYED - 1) {
                                alpha = stackState.alphaForLastAnimated.value
                            }
                        }
                }
                ) {
                    GifCard(gif = gif)
                }
            }
        }
    }
}