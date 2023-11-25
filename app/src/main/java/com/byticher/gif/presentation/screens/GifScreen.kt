package com.byticher.gif.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.byticher.gif.R
import com.byticher.gif.domain.Gif
import com.byticher.gif.presentation.components.cardstack.GifCardStack
import com.byticher.gif.presentation.components.MyTopBar
import com.byticher.gif.presentation.components.swipe.Direction
import com.byticher.gif.presentation.components.swipe.rememberSwipableCardState
import com.byticher.gif.const.TestTags
import com.byticher.gif.const.ConstValues.MAX_EMOJI_SIZE
import com.byticher.gif.data.FakeGifs
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.abs

val comicHelveticFont = FontFamily(
    Font(R.font.comic_helvetic_medium, FontWeight.Normal)
)

@Composable
fun GifScreen(
    visibleGifs: ImmutableList<Gif>,
    onDislikedClicked: () -> Unit = {},
    onLikedClicked: () -> Unit = {},
    onNextItem: (Direction?) -> Unit = {}
) {
    // General information
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val maxWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val maxHeight = with(density) { configuration.screenHeightDp.dp.toPx() }

    // Heights of composables
    val appTitleHeight = MAX_EMOJI_SIZE.dp
    val itemTitleHeight = 160.dp
    val gifStackHeight = configuration.screenHeightDp.dp - appTitleHeight - itemTitleHeight
    val gifStackHeightPx = with(density) { gifStackHeight.toPx() }

    // Current first visible item's title
    var showNextTitle by remember { mutableIntStateOf(0) }
    val title = remember(showNextTitle, visibleGifs.isNotEmpty()) {
        if (visibleGifs.isNotEmpty()) {
            if (showNextTitle == 0) {
                visibleGifs[0].title
            } else {
                visibleGifs[1].title
            }
        } else ""
    }

    // First Gif Card's state
    val swipableState = rememberSwipableCardState()

    val beforeAnimation = remember { { showNextTitle = 1 } }
    val onItemSwiped = remember(onNextItem) { {
        onNextItem(swipableState.direction)
        showNextTitle = 0
    } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .backgroundAnimation(
                xProvider = { swipableState.offset.value.x },
                width = maxWidth,
                height = maxHeight,
                backgroundColor = MaterialTheme.colorScheme.surface
            )
            .testTag(TestTags.GIF_SCREEN)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            MyTopBar(
                directionSwiped = swipableState.direction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(appTitleHeight),
                onDislikedClicked = onDislikedClicked,
                onLikedClicked = onLikedClicked
            )
            // Content
            if (visibleGifs.isNotEmpty()) {
                // Gif title
                AnimatedTitle(
                    title = title,
                    modifier = Modifier
                        .width(270.dp)
                        .height(itemTitleHeight)
                        .padding(bottom = 25.dp)
                        .testTag(TestTags.ANIMATED_TITLE)
                )
                // Stack of gifs
                GifCardStack(
                    visibleGifs = visibleGifs,
                    swipableState = swipableState,
                    modifier = Modifier
                        .height(gifStackHeight)
                    ,
                    layoutSize = Size(
                        width = maxWidth,
                        height = gifStackHeightPx
                    ),
                    beforeAnimation = beforeAnimation,
                    onItemSwiped = onItemSwiped
                )
            }
        }
    }
}

@Composable
fun AnimatedTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = title,
        label = TestTags.ANIMATED_TITLE,
        transitionSpec = {
            (slideInVertically(tween(800)) { height -> height }
                    + fadeIn(tween(1200)))
                .togetherWith(slideOutVertically(tween(700)) { height -> -height }
                        + fadeOut(tween(200)))
        },
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {targetState ->
        Text(
            text = targetState,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = comicHelveticFont,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.wrapContentHeight()
        )
    }
}

fun Modifier.backgroundAnimation(
    // Following best practices
    xProvider: () -> Float,
    width: Float,
    height: Float,
    backgroundColor: Color
) = this.drawBehind {
    val x = xProvider()
    if (x != 0f) {
        // Mathematics
        val radius = abs(x).coerceIn(0f, width)
        val offset = Offset(
            x = if (x > 0) width else 0f,
            y = if (radius < height * 2/7) height * 2/7 else radius
        )
        val brush = Brush.radialGradient(
            colors = listOf(
                if (x > 0) {
                    Color.Green.copy(alpha = alpha(x = x, width = width))
                } else {
                    Color.Yellow.copy(alpha = alpha(x = x, width = width))
                },
                backgroundColor
            ),
            radius = radius,
            center = offset
        )

        drawCircle(
            brush = brush,
            radius = radius,
            center = offset
        )
    }
}

private fun alpha(
    x: Float,
    width: Float
): Float {
    /* Alpha is going up in 0..width/4,
       stays the same till width/2,
       goes down in width/2..width*1.5f
     */
    return if (abs(x) < width/2) {
        (abs(x) / (width/4)).coerceIn(0f, 1f)
    } else {
        (-(abs(x) / width) + 1.5f).coerceIn(0f, 1f)
    }
}

@Preview
@Composable
fun GifScreenPreview() {
    var firstId by remember { mutableIntStateOf(1) }
    val gifs = FakeGifs.getGifs(firstId)

    MaterialTheme {
        Surface {
            GifScreen(
                visibleGifs = gifs,
                onNextItem = { firstId += 1 }
            )
        }
    }
}