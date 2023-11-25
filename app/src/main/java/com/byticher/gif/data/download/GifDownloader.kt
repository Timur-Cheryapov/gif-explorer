package com.byticher.gif.data.download

import com.byticher.gif.domain.Gif


interface GifDownloader {

    suspend fun downloadAndShareGif(gif: Gif): Boolean
}