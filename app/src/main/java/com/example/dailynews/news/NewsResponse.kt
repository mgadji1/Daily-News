package com.example.dailynews.news

data class NewsResponse(
    val status : String,
    val totalResults : Int,
    val articles : List<News>
)