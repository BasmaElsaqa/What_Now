package com.android.whatnownavigation.ui.logout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.whatnownavigation.Login
import com.google.firebase.auth.FirebaseAuth
import com.android.whatnownavigation.databinding.FragmentLogoutBinding

class LogoutFragment : Fragment() {

    private var _binding: FragmentLogoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val logoutViewModel = ViewModelProvider(this).get(LogoutViewModel::class.java)

        _binding = FragmentLogoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Logout Button logic
        val logoutButton: Button = binding.logoutButton // Assuming you have a logout button in your layout
        logoutButton.setOnClickListener {
            // Log out the user from Firebase
            FirebaseAuth.getInstance().signOut()

            // Redirect to the Login Activity
            val intent = Intent(activity, Login::class.java)
            startActivity(intent)
            activity?.finish() // Finish the current activity to prevent going back
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
