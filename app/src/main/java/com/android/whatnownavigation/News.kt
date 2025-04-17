package com.android.whatnownavigation

import com.google.gson.annotations.SerializedName

data class News(
    val articles:ArrayList<Article>
)

data class Article(
    val title :String,
    val url:String,
    val urlToImage :String,
    var isFavorite: Boolean = false
)
