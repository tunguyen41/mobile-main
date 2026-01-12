package com.sijanneupane.mvvmnews.api

import com.sijanneupane.mvvmnews.models.NewsResponse
import com.sijanneupane.mvvmnews.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    // GNews - Breaking News
    @GET("top-headlines")
    suspend fun getBreakingNews(
        @Query("country") countryCode: String = "us",
        @Query("lang") lang: String = "en",
        @Query("max") pageSize: Int = 20,
        @Query("page") pageNumber: Int = 1,
        @Query("token") apiKey: String = API_KEY
    ): Response<NewsResponse>


    // GNews - Search News
    @GET("search")
    suspend fun searchForNews(
        @Query("q") searchQuery: String,
        @Query("lang") lang: String = "en",
        @Query("max") pageSize: Int = 20,
        @Query("page") pageNumber: Int = 1,
        @Query("token") apiKey: String = API_KEY
    ): Response<NewsResponse>
}
