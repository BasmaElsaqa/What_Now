package com.example.whatnow6

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.whatnownavigation.Article
import com.android.whatnownavigation.R
import com.android.whatnownavigation.databinding.ArticleListItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class NewsAdapter(
    val a: Activity, val articles: ArrayList<Article>, val firestore: FirebaseFirestore,
    val favoritesCollection: CollectionReference
) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(){
    class NewsViewHolder(val binding: ArticleListItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val b = ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(b)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        Log.d("trace", "Link: ${articles[position].urlToImage}")
        holder.binding.articleText.text = articles[position].title
        Glide
            .with(holder.binding.articleImage.context)
            .load(articles[position].urlToImage)
            .error(R.drawable.broken_image)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.binding.articleImage)
        val url = articles[position].url
        holder.binding.articleContainer.setOnClickListener {

            val i = Intent(Intent.ACTION_VIEW, url.toUri())
            a.startActivity(i)
        }
        holder.binding.shareFab.setOnClickListener {
            ShareCompat
                .IntentBuilder(a)
                .setType("text/plain")
                .setChooserTitle("Share article with: ")
                .setText(url)
                .startChooser()
        }
        holder.binding.fav.setOnClickListener {

            val article = articles[position]

            if (article.isFavorite) {
                // Remove from Firestore if already favorite
                favoritesCollection.document(article.title).delete()
                article.isFavorite = false
                holder.binding.fav.setImageResource(R.drawable.star_outline)
            } else {
                // Add to Firestore if not favorite
                favoritesCollection.document(article.title).set(article)
                article.isFavorite = true
                holder.binding.fav.setImageResource(R.drawable.star_filled)
            }
        }

    }


}