package com.byticher.gif.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.byticher.gif.presentation.GifViewModel

@Composable
fun MainScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        val viewModel = hiltViewModel<GifViewModel>()
        val gifs = viewModel.gifPagingFlow.collectAsLazyPagingItems()

        viewModel.updateVisibleGifs(gifs)

        val context = LocalContext.current
        LaunchedEffect(key1 = gifs.loadState) {
            if (gifs.loadState.refresh is LoadState.Error) {
                Toast.makeText(
                    context,
                    "Error: " + (gifs.loadState.refresh as LoadState.Error).error.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Main content
        Box {
            HostScreen(viewModel = viewModel)

            if (gifs.loadState.refresh == LoadState.Loading) {
                val color = MaterialTheme.colorScheme.background
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawRect(color = color, size = size, topLeft = Offset.Zero)
                        }
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}