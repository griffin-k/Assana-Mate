package com.my.darkmatter

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login_Screen : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button


    private val validUsername = "admin"
    private val validPassword = "password123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        // Initialize views
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)

        // Set click listener for login button
        loginButton.setOnClickListener {
            // Validate the login fields
            if (validateLogin()) {
                // If valid, proceed to next activity
                val intent = Intent(this, MainActivity::class.java) // Replace with your next activity
                startActivity(intent)
                finish() // Optional: finish the current activity
            }
        }
    }

    private fun validateLogin(): Boolean {
        // Get input values
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Check if username or password is empty
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showToast("Username and Password cannot be empty")
            return false
        }

        // Check if credentials are correct
        if (username != validUsername || password != validPassword) {
            showToast("Invalid username or password")
            return false
        }

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
