package com.example.dailynews.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailynews.imageloader.GlideImageLoader
import com.example.dailynews.adapter.NewsViewHolder
import com.example.dailynews.R
import com.example.dailynews.news.News

class NewsAdapter(
    context : Context,
    private val layoutInflater: LayoutInflater,
    private val itemClick : (News) -> Unit
) : RecyclerView.Adapter<NewsViewHolder>() {
    private val imageLoader = GlideImageLoader(context)
    private val listOfNews = mutableListOf<News>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsViewHolder {
        val view = layoutInflater.inflate(R.layout.news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NewsViewHolder,
        position: Int
    ) {
        val news = listOfNews[position]

        val urlToImage = news.urlToImage ?: "None"
        val title = news.title ?: "None"
        val content = news.content ?: "None"

        if (urlToImage != "None") imageLoader.loadImage(urlToImage, holder.imageView)

        holder.tvTitle.text = title
        holder.tvContent.text = content

        holder.itemView.setOnClickListener {
            itemClick(news)
        }
    }

    override fun getItemCount(): Int = listOfNews.size

    fun addNews(news: News) {
        listOfNews.add(news)
        notifyItemInserted(listOfNews.lastIndex)
    }
}