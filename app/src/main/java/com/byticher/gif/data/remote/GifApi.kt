package com.byticher.gif.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface GifApi {

    @GET("trending")
    suspend fun getResponse(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("limit") pageSize: Int,
        @Query("offset") offset: Int,
        @Query("rating") rating: String = "g",
        @Query("bundle") bundle: String = "clips_grid_picker"
    ): GifApiResponse

    companion object {
        const val API_KEY = "gNXuS8ifZoMwwjDEs8zcyrAofgVNOQyH"
        const val BASE_URL = "https://api.giphy.com/v1/gifs/"
    }
}