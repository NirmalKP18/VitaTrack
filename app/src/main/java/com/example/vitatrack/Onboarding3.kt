package com.example.vitatrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Onboarding3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding3)

        val getStartedButton: Button = findViewById(R.id.getStartedButton)

        getStartedButton.setOnClickListener {
            val intent = Intent(this, Login::class.java) // Change to HomeActivity if needed
            startActivity(intent)
            finish()
        }
    }
}
