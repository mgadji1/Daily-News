package com.example.dailynews.news

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dailynews.NEWS_KEY
import com.example.dailynews.R
import com.example.dailynews.imageloader.GlideImageLoader

class NewsActivity : AppCompatActivity() {
    private val imageLoader = GlideImageLoader(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initData()
    }

    private fun initData() {
        val tvNewsTitle = findViewById<TextView>(R.id.tvNewsTitle)
        val tvNewsAuthor = findViewById<TextView>(R.id.tvNewsAuthor)
        val tvNewsPublishTime = findViewById<TextView>(R.id.tvNewsPublishTime)
        val newsImageView = findViewById<ImageView>(R.id.newsImageView)
        val tvNewsUrl = findViewById<TextView>(R.id.tvNewsUrl)
        val tvNewsContent = findViewById<TextView>(R.id.tvNewsContent)
        val btnClose = findViewById<Button>(R.id.btnClose)

        val news = intent.getParcelableExtra<News>(NEWS_KEY)

        val title = news?.title ?: "None"
        val formattedTitle = "Title: $title"

        val author = news?.author ?: "None"
        val formattedAuthor = "Author: $author"

        val publishTime = news?.publishedAt ?: "None"
        val formattedPublishTime = "Published: $publishTime"

        val imageUrl = news?.urlToImage ?: "None"

        val url = news?.url ?: "None"
        val formattedUrl = "Full article: $url"

        val content = news?.content ?: "None"
        val formattedContent = "Content: $content"

        tvNewsTitle.text = formattedTitle
        tvNewsAuthor.text = formattedAuthor
        tvNewsPublishTime.text = formattedPublishTime
        tvNewsUrl.text = formattedUrl
        tvNewsContent.text = formattedContent

        if (imageUrl != "None") imageLoader.loadImage(imageUrl, newsImageView)

        btnClose.setOnClickListener {
            finish()
        }
    }
}