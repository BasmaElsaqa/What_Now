package com.android.whatnownavigation.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.whatnownavigation.NewsActivity
import com.android.whatnownavigation.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        binding.buttonGeneral.setOnClickListener { openNewsActivity("general") }
        binding.buttonSports.setOnClickListener { openNewsActivity("sports") }
        binding.buttonBusiness.setOnClickListener { openNewsActivity("business") }
        binding.buttonTechnology.setOnClickListener { openNewsActivity("technology") }
        binding.buttonEntertainment.setOnClickListener { openNewsActivity("entertainment") }
        binding.buttonHealth.setOnClickListener { openNewsActivity("health") }
        binding.buttonScience.setOnClickListener { openNewsActivity("science") }
        return root
    }

    private fun openNewsActivity(category: String) {
        val intent = Intent(requireContext(), NewsActivity::class.java)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
