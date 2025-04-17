package com.android.whatnownavigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var progress: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Redirect if already logged in
        if (auth.currentUser != null) {
            navigateToHome()
            return // Stop further execution
        }

        val emailText = findViewById<EditText>(R.id.email_text)
        val passwordText = findViewById<EditText>(R.id.password_text)
        val loginBtn = findViewById<Button>(R.id.login_button)
        progress = findViewById(R.id.progress_bar)
        val notUser: TextView = findViewById(R.id.signup_link)
        val forgetPassword: TextView = findViewById(R.id.forgetpassword)

        loginBtn.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progress.visibility = View.VISIBLE
            loginBtn.isEnabled = false

            login(email, password)
        }

        notUser.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        forgetPassword.setOnClickListener {
            progress.isVisible = true
            val email = emailText.text.toString()
            if (email.isBlank()) {
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
                progress.isVisible = false
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    progress.isVisible = false
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email sent!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progress.visibility = View.GONE
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        navigateToHome()
                    } else {
                        Toast.makeText(this, "Check your email for verification", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Prevent back navigation to login screen
    }
}
