package com.example.dailynews

import com.example.dailynews.news.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApiService {
    @GET("everything")
    suspend fun getNews(
        @Query("q") keyword : String,
        @Query("from") from : String,
        @Query("to") to : String,
        @Query("pageSize") pageSize : Int,
        @Query("apiKey") apiKey : String
    ) : Response<NewsResponse>
}