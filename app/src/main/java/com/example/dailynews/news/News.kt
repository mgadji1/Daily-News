package com.example.dailynews.news

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class News(
    val author : String? = null,
    val title : String? = null,
    val url : String? = null,
    val urlToImage : String? = null,
    val publishedAt : String? = null,
    val content : String? = null
) : Parcelable
