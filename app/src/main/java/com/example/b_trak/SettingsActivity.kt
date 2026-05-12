package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

/**
 * SettingsActivity allows users to customize their application experience.
 * Currently, it manages the theme toggle (Dark/Light mode) and session termination (Logout).
 */
class SettingsActivity : AppCompatActivity() {

    /**
     * Initializes the settings UI, synchronizes the theme switch state, and sets up click listeners.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bind the Activity to the settings XML layout
        setContentView(R.layout.activity_settings)

        // Find the back arrow icon and set its listener
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            // Closes the settings screen and returns the user to the MainActivity dashboard
            finish()
        }

        // Locate the theme toggle switch in the layout
        val switchDarkMode = findViewById<Switch>(R.id.switch_dark_mode)
        
        // SYNCHRONIZATION: Determine the current system theme and set the switch's initial state accordingly
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        switchDarkMode.isChecked = currentMode == AppCompatDelegate.MODE_NIGHT_YES

        // THEME LOGIC: Set a listener to detect when the user flips the toggle
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // If switch is ON, globally apply the Dark Theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // If switch is OFF, globally apply the Light Theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Locate the logout button and define its reset logic
        val btnLogout = findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            // AUTH RESET: Prepare to return the user to the initial Login screen
            val intent = Intent(this, LoginActivity::class.java)
            // SECURITY: Clear the entire activity back-stack so the user cannot navigate back into the garage
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent) // Execute the transition
        }
    }
}