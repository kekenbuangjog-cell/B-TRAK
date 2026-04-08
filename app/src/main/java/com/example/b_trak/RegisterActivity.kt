package com.example.b_trak

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 1. Link the "LOGIN" text at the bottom
        val btnLoginLink = findViewById<TextView>(R.id.text_back_to_login)

        // 2. Link the "EXECUTE_REGISTRATION" button
        val btnRegister = findViewById<Button>(R.id.btn_register_account)

        // Navigation: Go back to Login screen
        btnLoginLink.setOnClickListener {
            // finish() simply closes this screen and reveals
            // the LoginActivity that is already sitting underneath.
            finish()
        }

        // Action: Placeholder for Sprint 2 Database work
        btnRegister.setOnClickListener {
            // For now, we just show a "vibe" message
            Toast.makeText(this, "INITIALIZING_DATA_UPLINK...", Toast.LENGTH_SHORT).show()

            // Logic for saving to SQLite goes here in the next sprint!
        }
    }
}