package com.android.whatnownavigation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        // Initialize views
        val emailText = findViewById<EditText>(R.id.email_text)
        val passwordText = findViewById<EditText>(R.id.password_text)
        val confirmPasswordText = findViewById<EditText>(R.id.repassword)
        val signUpButton = findViewById<Button>(R.id.signup_button)
        progress = findViewById(R.id.progress_bar)
        val alreadyUser: TextView = findViewById(R.id.already_user)

        // Sign Up Button Click Listener
        signUpButton.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()
            val confirmPassword = confirmPasswordText.text.toString().trim()

            // Validate inputs
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length <= 6) {
                Toast.makeText(this, "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show progress bar and disable button to prevent multiple clicks
            progress.visibility = View.VISIBLE
            signUpButton.isEnabled = false

            // Simulate network delay
            Handler(Looper.getMainLooper()).postDelayed({
                addUser(email, password)
            }, 3000)
        }

        alreadyUser.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    // Create new user
    private fun addUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progress.isVisible = false

                if (task.isSuccessful) {
                    verifyEmail()
                } else {
                    Toast.makeText(this, task.exception?.message ?: "Error adding user", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Send verification email
    private fun verifyEmail() {
        val user = auth.currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Check your email for verification", Toast.LENGTH_SHORT).show()
                    progress.visibility = View.GONE
                } else {
                    Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
