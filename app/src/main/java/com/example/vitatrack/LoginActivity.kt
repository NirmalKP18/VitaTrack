package com.example.vitatrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameField = findViewById<EditText>(R.id.inputUsername)
        val passwordField = findViewById<EditText>(R.id.inputPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val signupText = findViewById<TextView>(R.id.textCreateAccount)

        btnLogin.setOnClickListener {
            val inputUsername = usernameField.text.toString().trim()
            val inputPassword = passwordField.text.toString().trim()

            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean("isLoggedIn", true)
            editor.putString("username", inputUsername)
            editor.apply()

            val savedUsername = prefs.getString("username", null)
            val savedPassword = prefs.getString("password", null)

            if (inputUsername == savedUsername && inputPassword == savedPassword) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        signupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
