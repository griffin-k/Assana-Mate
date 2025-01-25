package com.my.darkmatter

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Signup_Screen : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_screen)

        // Initialize views
        usernameEditText = findViewById(R.id.username)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        signupButton = findViewById(R.id.loginButton)

        signupButton.setOnClickListener {

            if (validateFields()) {
                val intent = Intent(this, MainActivity::class.java) // Replace with your next activity
                startActivity(intent)
            }
        }
    }

    private fun validateFields(): Boolean {
        // Validate username
        val username = usernameEditText.text.toString().trim()
        if (TextUtils.isEmpty(username)) {
            showToast("Username is required")
            return false
        }

        // Validate email
        val email = emailEditText.text.toString().trim()
        if (TextUtils.isEmpty(email) || !email.contains("@")) {
            showToast("Valid email is required")
            return false
        }

        // Validate password
        val password = passwordEditText.text.toString().trim()
        if (TextUtils.isEmpty(password)) {
            showToast("Password is required")
            return false
        }

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
