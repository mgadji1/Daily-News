package com.example.dailynews

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailynews.adapter.NewsAdapter
import com.example.dailynews.api.NewsApiService
import com.example.dailynews.news.News
import com.example.dailynews.news.NewsActivity
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val BASE_URL = "https://newsapi.org/v2/"
const val NEWS_KEY = "news"

class MainActivity : AppCompatActivity() {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val newsApiService = retrofit.create(NewsApiService::class.java)

    private lateinit var recyclerView : RecyclerView

    private lateinit var adapter : NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = NewsAdapter(this, layoutInflater) { news ->
            val intent = Intent(this, NewsActivity::class.java)
            intent.putExtra(NEWS_KEY, news)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val dates = getDates()

        val today = dates[0]
        val yesterday = dates[1]

        lifecycleScope.launch {
            getTodayNews(today, yesterday)
        }
    }

    private fun getDates() : Array<String> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val today = LocalDate.now()
            val formattedToday = today.format(formatter)

            val yesterday = today.minusDays(1)
            val formattedYesterday = yesterday.format(formatter)

            return arrayOf(formattedToday, formattedYesterday)
        }
        return arrayOf()
    }

    private suspend fun getTodayNews(formattedToday : String, formattedYesterday : String) {
        val NEWS_API_KEY = BuildConfig.NEWS_API_KEY
        val result = newsApiService.getNews("*", formattedYesterday,
            formattedToday, 10, NEWS_API_KEY)

        if (result.isSuccessful) {
            val newsResponse = result.body()
            val articles = newsResponse?.articles ?: emptyList()

            for (article in articles) {
                val author = article.author
                val url = article.url
                val publishTime = article.publishedAt
                val imageUrl = article.urlToImage
                val title = article.title
                val htmlContent = article.content

                val content = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY).toString()

                val news = News(
                    author,
                    title,
                    url,
                    imageUrl,
                    publishTime,
                    content
                )

                adapter.addNews(news)
            }
        }
    }
}