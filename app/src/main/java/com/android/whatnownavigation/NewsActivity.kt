package com.android.whatnownavigation

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.android.whatnownavigation.databinding.ActivityNewsBinding
import com.example.whatnow6.NewsAdapter
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private var category: String = "general"
    private var country: String = "us"
    private var language: String = "en"
    private val firestore = FirebaseFirestore.getInstance()
    private val favoritesCollection: CollectionReference = firestore.collection("favorites")
    private lateinit var sharedPreferences: SharedPreferences
    private val SETTINGS_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("language_data", MODE_PRIVATE)

        category = intent.getStringExtra("CATEGORY") ?: "general"
        country = intent.getStringExtra("COUNTRY") ?: "us"
        language = intent.getStringExtra("LANGUAGE") ?: "en"
        // Retrieve category from intent
        loadNews(category, country,language )

        binding.swipeRefresh.setOnRefreshListener {
            loadNews(category,country,language)
        }

    }

    private fun loadSavedNews() {
        val savedCategory = sharedPreferences.getString("category", "general") ?: "general"
        val savedLanguage = sharedPreferences.getString("language", "en") ?: "en"
        val savedCountry = sharedPreferences.getString("country", "us") ?: "us"
        loadNews(savedCategory ,savedCountry, savedLanguage)
    }

    private fun loadNews(category: String, country: String,language: String) {
        Log.d("trace", "Loading news for country: $country, language: $language")

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit
            .Builder()
            .baseUrl("https://newsapi.org")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val c=retrofit.create(NewsCallable::class.java)
        c.getNews(category, country,language).enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news = response.body()
                val articles=news?.articles!!

                articles.removeAll {
                    it.title == "[Removed]"
                }

                runOnUiThread {  // Ensure UI updates on the main thread
                    showNews(articles)
                    binding.progress.isVisible = false
                    binding.swipeRefresh.isRefreshing = false
                }

                showNews(articles)
                binding.progress.isVisible = false
                binding.swipeRefresh.isRefreshing = false
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("trace", "Error: ${t.message}")
                binding.progress.isVisible = false
                binding.swipeRefresh.isRefreshing = false
            }
        })
    }

    private fun showNews(articles: ArrayList<Article>) {
        val adapter = NewsAdapter(this, articles, firestore  , favoritesCollection)
        binding.newsList.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            loadSavedNews()
        }
    }
}
