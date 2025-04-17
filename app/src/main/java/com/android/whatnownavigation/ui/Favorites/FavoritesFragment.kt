package com.android.whatnownavigation.ui.Favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.whatnownavigation.databinding.FragmentFavoritesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.android.whatnownavigation.Article
import com.android.whatnownavigation.FavoriteAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val favoritesCollection = firestore.collection("favorites")
    private lateinit var favoriteArticles: ArrayList<Article>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ViewModel for Fragment, if you need it
        val favoritesViewModel =
            ViewModelProvider(this).get(FavoritesViewModel::class.java)

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        loadFavorites()  // Load favorites from Firestore

        return root
    }

    // Function to load favorites from Firestore
    private fun loadFavorites() {
        binding.progress.visibility = View.VISIBLE
        binding.emptyFavoritesText.visibility = View.GONE

        favoritesCollection.get()
            .addOnSuccessListener { documents ->
                favoriteArticles = ArrayList()
                for (document in documents) {
                    val article = document.toObject(Article::class.java).apply {
                        isFavorite = true // Ensure all are marked as favorites
                    }
                    favoriteArticles.add(article)
                }

                if (favoriteArticles.isEmpty()) {
                    binding.emptyFavoritesText.visibility = View.VISIBLE
                } else {
                    displayFavorites()
                }
                binding.progress.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                binding.progress.visibility = View.GONE
                binding.emptyFavoritesText.visibility = View.VISIBLE
                binding.emptyFavoritesText.text = "Error loading favorites"
            }
    }

    // Function to display favorites in the list
    private fun displayFavorites() {
        val adapter = FavoriteAdapter(requireContext(), favoriteArticles)
        binding.favoriteList.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}