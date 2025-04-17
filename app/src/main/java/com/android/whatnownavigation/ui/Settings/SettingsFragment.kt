package com.android.whatnownavigation.ui.Settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.whatnownavigation.NewsActivity
import com.android.whatnownavigation.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("language_data", Context.MODE_PRIVATE)

        // Retrieve and set the saved country
        val savedCountry = sharedPreferences.getString("country", "us")
        when (savedCountry) {
            "us" -> binding.radioUs.isChecked = true
            "eg" -> binding.radioEgy.isChecked = true
            "fr" -> binding.radioFr.isChecked = true
        }

        // Setup category spinner
        val categories = listOf("general", "business", "entertainment", "health", "science", "sports", "technology")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter

        // Save button click listener
        binding.saveBtn.setOnClickListener {
            val country = when (binding.radioGroup.checkedRadioButtonId) {
                binding.radioFr.id -> "fr"
                binding.radioUs.id -> "us"
                binding.radioEgy.id -> "zh"
                else -> "us"
            }

            val language = when (country) {
                "fr" -> "fr"
                "us" -> "en"
                "zh" -> "zh"
                else -> "en"
            }

            val selectedCategory = binding.categorySpinner.selectedItem.toString()

            // Save preferences
            sharedPreferences.edit()
                .putString("country", country)
                .putString("language", language)
                .apply()

            // Navigate to NewsActivity with the selected category
            val intent = Intent(requireContext(), NewsActivity::class.java).apply {
                putExtra("CATEGORY", selectedCategory)
            }
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
