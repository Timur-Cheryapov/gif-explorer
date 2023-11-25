package com.byticher.gif.data

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import com.byticher.gif.data.local.GifEntity
import com.byticher.gif.data.remote.GifApiResponse
import com.byticher.gif.data.remote.GifDto
import com.byticher.gif.domain.Gif

fun GifDto.toGifEntity(): GifEntity {
    return GifEntity(
        id = id,
        idString = idString,
        title = title,
        url = url
    )
}

fun GifEntity.toGif(): Gif {
    return Gif(
        id = id,
        title = title
            .split(" ")
            .filter { it.toLowerCase(Locale.current) != "gif" }
            .joinToString(" "),
        url = url
    )
}

fun GifApiResponse.toListOfGifDto(): List<GifDto> {
    var i = pagination.offset + 1
    return data.map {
        GifDto(
            id = i++,
            idString = it.idString,
            title = it.title,
            url = it.images.original.url
        )
    }
}
