package com.android.whatnownavigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.android.whatnownavigation.databinding.ArticleListItemBinding
import com.android.whatnownavigation.databinding.FavoritesListItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class FavoriteAdapter(
    private val context: Context,
    private val favoriteArticles: ArrayList<Article>
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(val binding: ArticleListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ArticleListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val article = favoriteArticles[position]

        // Set article title
        holder.binding.articleText.text = article.title ?: "No title available"

        // Load image with error handling
        try {
            Glide.with(context)
                .load(article.urlToImage)
                .error(R.drawable.broken_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.articleImage)
        } catch (e: Exception) {
            holder.binding.articleImage.setImageResource(R.drawable.broken_image)
        }

        // Set filled star icon for favorites
        holder.binding.fav.setImageResource(R.drawable.star_filled)
        holder.binding.fav.alpha = 1f

        // Handle article click
        holder.binding.articleContainer.setOnClickListener {
            try {
                article.url?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Couldn't open article", Toast.LENGTH_SHORT).show()
            }
        }

        // Hide share button if not needed
        holder.binding.shareFab.visibility = View.GONE
    }

    override fun getItemCount() = favoriteArticles.size
}