package com.byticher.gif.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface GifDao {

    @Upsert
    suspend fun upsertAll(gifs: List<GifEntity>)

    @Query("SELECT * FROM gifentity")
    fun pagingSource(): PagingSource<Int, GifEntity>

    @Query("DELETE FROM gifentity")
    suspend fun clearAll()
}