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

        // Go to Signup page
        createAccountText.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Go to HomeActivity (main screen)
        loginButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // closes LoginActivity so user can't go back
        }
    }
}
