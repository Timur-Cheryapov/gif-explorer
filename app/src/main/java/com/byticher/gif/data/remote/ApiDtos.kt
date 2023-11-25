package com.byticher.gif.data.remote

import com.squareup.moshi.Json

data class GifApiResponse(
    val data: List<DataDto>,
    val pagination: PaginationDto
)

data class PaginationDto(
    val offset: Int
)

data class DataDto(
    @field:Json(name = "id") val idString: String,
    val title: String,
    val images: ImagesDto
)

data class ImagesDto(
    val original: OriginalDto
)

data class OriginalDto(
    val url: String
)

// Network model
data class GifDto(
    val id: Int,
    val idString: String,
    val title: String,
    val url: String
)