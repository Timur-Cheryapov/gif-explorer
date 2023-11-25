package com.byticher.gif.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GifEntity::class],
    version = 5
)
abstract class GifDatabase: RoomDatabase() {

    abstract val dao: GifDao
}