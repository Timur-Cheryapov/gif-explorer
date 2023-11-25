package com.byticher.gif.presentation.screens

import android.os.Vibrator
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.byticher.gif.R
import com.byticher.gif.presentation.GifViewModel
import com.byticher.gif.domain.clickMedium
import com.byticher.gif.presentation.components.swipe.Direction

@Composable
fun HostScreen(
    viewModel: GifViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val vibrator = remember {
        context.getSystemService(Vibrator::class.java)
    }

    val navController = rememberNavController()
    val onDislikedClicked = remember { {
        vibrator.clickMedium()
        navController.navigate("dislikedscreen")
    } }
    val onLikedClicked = remember { {
        vibrator.clickMedium()
        navController.navigate("likedscreen")
    } }
    val updateVisibleGifs = remember {
        { direction: Direction? ->
            viewModel.updateVisibleGifs(direction)
        }
    }

    NavHost(
        navController = navController,
        startDestination = "gifscreen",
        modifier = Modifier.fillMaxSize()
    ) {
        // Gif Screen
        composable("gifscreen") {
            GifScreen(
                visibleGifs = uiState.visibleGifs,
                onDislikedClicked = onDislikedClicked,
                onLikedClicked = onLikedClicked,
                onNextItem = updateVisibleGifs
            )
        }

        // Disliked gifs Chosen Gifs Screen
        composable(
            route = "dislikedscreen",
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(200, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            }
        ) {
            ChosenGifsScreen(
                title = stringResource(R.string.disliked_gifs),
                chosenGifs = uiState.dislikedGifs
            )
        }

        // Liked gifs Chosen Gifs Screen
        composable(
            route = "likedscreen",
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(200, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            ChosenGifsScreen(
                title = stringResource(R.string.favourite_gifs),
                chosenGifs = uiState.likedGifs
            )
        }
    }
}