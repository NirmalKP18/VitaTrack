package com.example.vitatrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val createAccountText: TextView = findViewById(R.id.createAccountText)
        val loginButton: Button = findViewById(R.id.loginButton)

        // Navigate to Signup screen when "Create new account" is clicked
        createAccountText.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Login button (no action yet)
        loginButton.setOnClickListener {
            // TODO: Add login functionality later
        }
    }
}
