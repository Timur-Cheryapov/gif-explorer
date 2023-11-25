package com.byticher.gif.presentation

import com.byticher.gif.domain.Gif
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class GifUiState(
    val firstVisibleIndex: Int = 0,
    val visibleGifs: ImmutableList<Gif> = persistentListOf(),
    // Liked and disliked gifs are saved only
    // during the activity lifecycle due to simplicity of the app
    val likedGifs: ImmutableList<Gif> = persistentListOf(),
    val dislikedGifs: ImmutableList<Gif> = persistentListOf()
)
