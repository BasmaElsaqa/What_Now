package com.android.whatnownavigation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsCallable {
    @GET("v2/top-headlines")
    fun getNews(
        @Query("category") category: String,
        @Query("country") country: String = "us",
        @Query("language") language: String,
        @Query("apiKey") apiKey: String = "7c91d87e75cd45be854f0617aa0af3c9"
    ): Call<News>
}



