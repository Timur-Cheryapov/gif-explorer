package com.byticher.gif.data

import com.byticher.gif.domain.Gif
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class FakeGifs {

    companion object {
        fun gifTitle(id: Int) = "Amazing Gif #$id"

        fun getGifs(firstId: Int): ImmutableList<Gif> {
            val gifs = MutableList(5) {
                Gif(
                    id = firstId + it,
                    title = gifTitle(firstId + it),
                    url = ""
                )
            }.toPersistentList()

            return gifs
        }
    }
}