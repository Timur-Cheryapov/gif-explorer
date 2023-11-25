package com.byticher.gif.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.map
import com.byticher.gif.const.ConstValues.ITEM_AMOUNT_DISPLAYED
import com.byticher.gif.data.local.GifEntity
import com.byticher.gif.data.toGif
import com.byticher.gif.domain.Gif
import com.byticher.gif.presentation.components.swipe.Direction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GifViewModel @Inject constructor(
    pager: Pager<Int, GifEntity>
): ViewModel() {

    val gifPagingFlow = pager
        .flow
        .map { pagingData ->
            pagingData.map { it.toGif() }
        }
        .cachedIn(viewModelScope)

    // Will be assigned right after the creation of the viewModel in entry composable
    private lateinit var gifs: LazyPagingItems<Gif>

    private val _uiState = MutableStateFlow(GifUiState())
    val uiState: StateFlow<GifUiState> = _uiState.asStateFlow()

    fun updateVisibleGifs(direction: Direction? = null) {
        var firstVisibleIndex = uiState.value.firstVisibleIndex

        if (direction != null) {
            val firstVisibleItem = uiState.value.visibleGifs[0]

            if (direction == Direction.LEFT) addDislikedGif(firstVisibleItem)
            else addLikedGif(firstVisibleItem)

            firstVisibleIndex += 1
        }

        val gifsRange = getGifsRange(gifs, firstVisibleIndex)
        val gifsList = getGifsList(gifs, gifsRange)

        _uiState.update { currentState ->
            currentState.copy(
                firstVisibleIndex = firstVisibleIndex,
                visibleGifs = gifsList.toPersistentList()
            )
        }
    }

    fun updateVisibleGifs(newGifs: LazyPagingItems<Gif>) {
        gifs = newGifs
        updateVisibleGifs()
    }

    private fun addLikedGif(gif: Gif) {
        _uiState.update { currentState ->
            if (!uiState.value.likedGifs.contains(gif)){
                currentState.copy(
                    likedGifs = uiState.value.likedGifs.plus(gif).toPersistentList()
                )
            } else {
                currentState
            }
        }
    }

    private fun addDislikedGif(gif: Gif) {
        _uiState.update { currentState ->
            if (!uiState.value.dislikedGifs.contains(gif)) {
                currentState.copy(
                    dislikedGifs = uiState.value.dislikedGifs.plus(gif).toPersistentList()
                )
            } else {
                currentState
            }
        }
    }

    private fun getGifsRange(
        gifs: LazyPagingItems<Gif>,
        firstVisibleItem: Int
    ): IntRange {
        return if (gifs.itemSnapshotList.items.count() >= ITEM_AMOUNT_DISPLAYED) {
            firstVisibleItem..(ITEM_AMOUNT_DISPLAYED - 1 + firstVisibleItem)
        } else IntRange.EMPTY
    }

    private fun getGifsList(
        gifs: LazyPagingItems<Gif>,
        range: IntRange
    ): List<Gif> {
        val gifsList = mutableListOf<Gif>()
        for (i in range) {
            val gif = gifs[i]
            if (gif != null) gifsList.add(gif)
        }
        return gifsList.toList()
    }
}