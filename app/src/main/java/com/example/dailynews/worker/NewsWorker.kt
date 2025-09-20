package com.example.dailynews.worker

import android.content.Context
import android.os.Build
import android.text.Html
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dailynews.BASE_URL
import com.example.dailynews.BuildConfig
import com.example.dailynews.api.NewsApiService
import com.example.dailynews.news.NewsResponse
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NewsWorker(
    context : Context,
    workerParams : WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val newsApiService = retrofit.create(NewsApiService::class.java)

    override suspend fun doWork(): Result {
        getTodayNews()
        return Result.success()
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

    private fun sendNewsToActivity(result : Response<NewsResponse>) {
        val newsResponse = result.body()
        val articles = newsResponse?.articles ?: emptyList()

        val prefs = applicationContext.getSharedPreferences("news_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val jsonArray = JSONArray()

        for (article in articles) {
            val author = article.author ?: "None"
            val url = article.url ?: "None"
            val publishTime = article.publishedAt ?: "None"
            val imageUrl = article.urlToImage ?: "None"
            val title = article.title ?: "None"
            val htmlContent = article.content ?: "None"

            val content = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY).toString()

            val jsonNews = JSONObject()

            jsonNews.put("author", author)
            jsonNews.put("url", url)
            jsonNews.put("publishedAt", publishTime)
            jsonNews.put("imageUrl", imageUrl)
            jsonNews.put("title", title)
            jsonNews.put("content", content)

            jsonArray.put(jsonNews)
        }

        editor.putString("latest_news", jsonArray.toString())
        editor.apply()
    }

    private suspend fun getTodayNews() {
        val dates = getDates()

        val today = dates[0]
        val yesterday = dates[1]

        val NEWS_API_KEY = BuildConfig.NEWS_API_KEY
        val result = newsApiService.getNews("*", today,
            yesterday, 10, NEWS_API_KEY)

        if (result.isSuccessful) {
            sendNewsToActivity(result)
        }
    }
}