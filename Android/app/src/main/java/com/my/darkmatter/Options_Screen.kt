package com.my.darkmatter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Options_Screen : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options_screen)

        // Initialize views
        loginButton = findViewById(R.id.loginbtn)
        signupButton = findViewById(R.id.signupbtn)

        // Set click listener for Login button
        loginButton.setOnClickListener {
            val loginIntent = Intent(this, Login_Screen::class.java)
            startActivity(loginIntent)
        }

        // Set click listener for Signup button
        signupButton.setOnClickListener {
            val signupIntent = Intent(this, Signup_Screen::class.java)
            startActivity(signupIntent)
        }
    }
}
