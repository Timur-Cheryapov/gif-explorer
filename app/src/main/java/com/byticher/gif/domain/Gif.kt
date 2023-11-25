package com.byticher.gif.domain

import androidx.compose.runtime.Stable

// UI/domain model
@Stable
data class Gif (
    val id: Int,
    val title: String,
    val url: String
)