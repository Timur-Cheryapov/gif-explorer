package com.byticher.gif.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.byticher.gif.const.TestTags
import com.byticher.gif.domain.Gif
import com.byticher.gif.ui.theme.GifTheme

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GifCard(
    gif: Gif,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    Card(
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    ) {
        // Glide doesn't have a convenient placeholder,
        // so I will imitate image loading for better user experience
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
            GlideImage(
                model = gif.url,
                contentDescription = gif.title,
                contentScale = contentScale,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(TestTags.getGifCardTagWithId(gif.id))
            )
        }
    }
}

@Preview
@Composable
fun GifCardPreview() {
    GifTheme {
        GifCard(
            gif = Gif(
                id = 1,
                title = "Very Funny",
                url = "Brother"
            )
        )
    }
}