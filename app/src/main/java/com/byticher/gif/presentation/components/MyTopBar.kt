package com.byticher.gif.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.byticher.gif.R
import com.byticher.gif.const.TestTags
import com.byticher.gif.const.ConstValues.INITIAL_EMOJI_SIZE
import com.byticher.gif.const.ConstValues.MAX_EMOJI_SIZE
import com.byticher.gif.presentation.components.swipe.Direction
import com.byticher.gif.ui.theme.GifTheme

@Composable
fun MyTopBar(
    modifier: Modifier = Modifier,
    directionSwiped: Direction? = null,
    onDislikedClicked: () -> Unit = {},
    onLikedClicked: () -> Unit = {}
) {
    // Animatable size for emojis
    val scale = remember {
        listOf(Animatable(1f), Animatable(1f))
    }

    // Emojis' scale animation
    LaunchedEffect(key1 = directionSwiped) {
        when(directionSwiped) {
            Direction.LEFT -> {
                scale[0].animateTo(MAX_EMOJI_SIZE / INITIAL_EMOJI_SIZE)
                scale[0].animateTo(1f)
            }
            Direction.RIGHT -> {
                scale[1].animateTo(MAX_EMOJI_SIZE / INITIAL_EMOJI_SIZE)
                scale[1].animateTo(1f)
            }
            else -> {  }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.testTag(TestTags.MY_TOP_BAR)
    ) {
        // Icon "Bad"
        IconButton(
            onClick = onDislikedClicked,
            modifier = Modifier.size(MAX_EMOJI_SIZE.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sad_smile),
                contentDescription = stringResource(R.string.disliked_section_button),
                tint = Color.Unspecified,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale[0].value
                        scaleY = scale[0].value
                    }
            )
        }
        // App title
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displayLarge,
            fontFamily = FontFamily(
                Font(R.font.versus_font, FontWeight.Normal)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentHeight()
        )
        // Icon "Good"
        IconButton(
            onClick = onLikedClicked,
            modifier = Modifier.size(MAX_EMOJI_SIZE.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.funny_smile),
                contentDescription = stringResource(R.string.favourite_section_button),
                tint = Color.Unspecified,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale[1].value
                        scaleY = scale[1].value
                    }
            )
        }
    }
}

@Preview
@Composable
fun MyTopBarPreview() {
    GifTheme {
        MyTopBar(Modifier.fillMaxWidth())
    }
}