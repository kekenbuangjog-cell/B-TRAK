package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // NAVIGATION: Return to previous station (MainActivity)
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        // PROTOCOL: Theme Switching Logic
        val switchDarkMode = findViewById<Switch>(R.id.switch_dark_mode)
        
        // SYNC_STATE: Set the switch to match the current theme mode
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        switchDarkMode.isChecked = currentMode == AppCompatDelegate.MODE_NIGHT_YES

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // ACTION: Enable Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // ACTION: Enable Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // PROTOCOL: Session Termination (Logout)
        val btnLogout = findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            // AUTH_RESET: Clear activity stack and return to Login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}