package com.byticher.gif.presentation.screens

import android.os.Build
import android.os.Vibrator
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.byticher.gif.R
import com.byticher.gif.const.TestTags
import com.byticher.gif.const.ConstValues.CELLS_NUMBER
import com.byticher.gif.const.ConstValues.MAX_EMOJI_SIZE
import com.byticher.gif.data.download.GifDownloaderImpl
import com.byticher.gif.domain.Gif
import com.byticher.gif.domain.doubleClick
import com.byticher.gif.presentation.components.GifCard
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChosenGifsScreen(
    title: String,
    chosenGifs: ImmutableList<Gif>
) {
    // width = height of item
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp
    val titleHeight = MAX_EMOJI_SIZE.dp
    val color = MaterialTheme.colorScheme.surface

    // Chosen gif for reviewing (will be opened in dialog)
    var chosenGif by remember {
        mutableStateOf<Gif?>(null)
    }
    val onDismiss = remember { { chosenGif = null } }

    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        // Dialog for reviewing Gif
        if (chosenGif != null) {
            ChosenGifReview(
                gif = chosenGif!!,
                onDismiss = onDismiss
            )
        }
        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
        ) {
            // Grid of gifs
            if (chosenGifs.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(CELLS_NUMBER),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                ) {
                    items(CELLS_NUMBER) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(titleHeight)
                        )
                    }
                    items(chosenGifs) {
                        GifCard(
                            gif = it,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                // width == height of item
                                .height(maxWidth / CELLS_NUMBER)
                                .clickable { chosenGif = it }
                        )
                    }
                }
            }
            Column {
                // Screen title
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fadingEdge()
                        .background(color)
                        .height(titleHeight)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.displaySmall,
                        fontFamily = comicHelveticFont,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }
                // No gifs message
                if (chosenGifs.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            // No gifs found image
                            Image(
                                painter = painterResource(id = R.drawable.no_files_found),
                                contentDescription = null,
                                modifier = Modifier.width(maxWidth / 2)
                            )
                            // No gifs found text
                            Text(
                                text = stringResource(R.string.there_are_no_gifs),
                                style = MaterialTheme.typography.titleSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalGlideComposeApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun ChosenGifReview(
    gif: Gif,
    onDismiss: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val downloader = remember { GifDownloaderImpl(context) }
    val vibrator = remember {
        context.getSystemService(Vibrator::class.java)
    }

    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(300.dp)
                .clickable(onClick = onDismiss)
        ) {
            Text(
                text = gif.title,
                fontFamily = comicHelveticFont,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .wrapContentSize()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            )
            Spacer(Modifier.height(32.dp))
            GlideImage(
                model = gif.url,
                contentDescription = gif.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    // Glide image doesn't provide any suitable sizes,
                    // so I have to improvise
                    .aspectRatio(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .combinedClickable(
                        onClick = onDismiss,
                        onLongClick = {
                            scope.launch(Dispatchers.IO) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    vibrator.doubleClick()
                                }
                                try {
                                    downloader.downloadAndShareGif(gif)
                                } catch (e: Exception) {
                                    if (e is CancellationException) throw e
                                    e.printStackTrace()
                                }
                            }
                        }
                    )
                    .testTag(TestTags.GIF_IN_REVIEW)
            )
        }
    }
}

fun Modifier.fadingEdge(
    brush: Brush = Brush.verticalGradient(0.8f to Color.Red, 1f to Color.Transparent)
) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleAlpha(0.0f,0.0f,0.0f,0.0f)
}