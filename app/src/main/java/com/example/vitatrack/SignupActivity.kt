package com.example.vitatrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val emailField = findViewById<EditText>(R.id.inputEmail)
        val usernameField = findViewById<EditText>(R.id.inputUsername)
        val passwordField = findViewById<EditText>(R.id.inputPassword)
        val confirmPasswordField = findViewById<EditText>(R.id.inputConfirmPassword)
        val btnCreate = findViewById<Button>(R.id.btnCreateAccount)
        val loginText = findViewById<TextView>(R.id.textLogin)

        btnCreate.setOnClickListener {
            val email = emailField.text.toString().trim()
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save user data
            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString("email", email)
            editor.putString("username", username)
            editor.putString("password", password)
            editor.apply()

            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
