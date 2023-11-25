package com.byticher.gif.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Database model
@Entity
data class GifEntity (
    @PrimaryKey
    val id: Int,
    val idString: String,
    val title: String,
    val url: String
)