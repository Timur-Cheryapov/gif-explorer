package com.byticher.gif.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.byticher.gif.data.local.GifDatabase
import com.byticher.gif.data.local.GifEntity
import com.byticher.gif.data.toGifEntity
import com.byticher.gif.data.toListOfGifDto
import retrofit2.HttpException
import java.io.IOException

// Controls the data flow between network and local database
@OptIn(ExperimentalPagingApi::class)
class GifRemoteMediator(
    private val gifDb: GifDatabase,
    private val gifApi: GifApi
): RemoteMediator<Int, GifEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GifEntity>
    ): MediatorResult {
        return try {
            // Offset for the first item
            val offset = when(loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }
                    lastItem.id
                }
            }

            //  Fetch new items
            val gifs = gifApi.getResponse(
                offset = offset,
                pageSize = state.config.pageSize
            ).toListOfGifDto()

            // Insert new items in database
            gifDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    gifDb.dao.clearAll()
                }
                val gifEntities = gifs.map { it.toGifEntity() }
                gifDb.dao.upsertAll(gifEntities)
            }

            // Return success
            MediatorResult.Success(
                endOfPaginationReached = gifs.isEmpty()
            )
        } catch(e: IOException) {
            MediatorResult.Error(e)
        } catch(e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}