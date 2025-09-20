package com.example.dailynews

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dailynews.adapter.NewsAdapter
import com.example.dailynews.news.News
import com.example.dailynews.news.NewsActivity
import com.example.dailynews.worker.NewsWorker
import org.json.JSONArray
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://newsapi.org/v2/"
const val NEWS_KEY = "news"

class MainActivity : AppCompatActivity() {
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

        recyclerView = findViewById(R.id.recyclerView)
        adapter = NewsAdapter(this, layoutInflater) { news ->
            val intent = Intent(this, NewsActivity::class.java)
            intent.putExtra(NEWS_KEY, news)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        makeRequest()
        displayNews()
    }

    private fun makeRequest() {
        val newsRequest = PeriodicWorkRequestBuilder<NewsWorker>(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "news_check",
            ExistingPeriodicWorkPolicy.KEEP,
            newsRequest
        )
    }

    private fun displayNews() {
        Log.d("Display", "display")
        val prefs = getSharedPreferences("news_prefs", MODE_PRIVATE)
        val json = prefs.getString("latest_news", null)

        Log.d("Json", "$json")

        if (!json.isNullOrEmpty()) {
            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                val news = News(
                    obj.optString("author", "None"),
                    obj.optString("title", "None"),
                    obj.optString("url", "None"),
                    obj.optString("imageUrl", "None"),
                    obj.optString("publishedAt", "None"),
                    obj.optString("content", "None")
                )

                adapter.addNews(news)
            }
        }
    }
}