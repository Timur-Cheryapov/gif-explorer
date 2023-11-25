package com.byticher.gif.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.byticher.gif.data.local.GifDatabase
import com.byticher.gif.data.local.GifEntity
import com.byticher.gif.data.remote.GifApi
import com.byticher.gif.data.remote.GifRemoteMediator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGifDatabase(@ApplicationContext context: Context): GifDatabase {
        return Room.databaseBuilder(
            context,
            GifDatabase::class.java,
            "gifs.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideGifApi(): GifApi {
        return Retrofit.Builder()
            .baseUrl(GifApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideGifPager(gifDb: GifDatabase, gifApi: GifApi): Pager<Int, GifEntity> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = GifRemoteMediator(
                gifDb = gifDb,
                gifApi = gifApi
            ),
            pagingSourceFactory = {
                gifDb.dao.pagingSource()
            }
        )
    }
}