package com.example.vitatrack

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay 3 seconds before moving to next activity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, Onboarding1::class.java) // Change to HomeActivity if needed
            startActivity(intent)
            finish() // Close splash so user can’t go back
        }, 3000) // 3000 ms = 3 seconds
    }
}
