package com.example.dailynews

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val newsApiService = retrofit.create(NewsApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val today = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedToday = today.format(formatter)

        val yesterday = today.minusDays(1)
        val formattedYesterday = yesterday.format(formatter)


        lifecycleScope.launch {
            getTodayNews(formattedToday, formattedYesterday)
        }
    }

    private suspend fun getTodayNews(formattedToday : String, formattedYesterday : String) {
        val NEWS_API_KEY = BuildConfig.NEWS_API_KEY
        val result = newsApiService.getNews("*", formattedYesterday,
            formattedToday, 10, NEWS_API_KEY)
        Log.d("Retrofit URL", result.errorBody()?.string().orEmpty())
        if (result.isSuccessful) {
            val newsResponse = result.body()
            Log.d("Response Status : ", "${newsResponse?.status}")
            Log.d("Number of articles : ", "${newsResponse?.articles?.size}")
            Log.d("Response body : ", "$newsResponse")
        }
    }
}