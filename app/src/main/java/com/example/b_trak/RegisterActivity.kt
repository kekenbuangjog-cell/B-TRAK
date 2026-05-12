package com.example.b_trak

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * RegisterActivity provides the UI for creating a new user account.
 * In the current prototype phase, it serves as a visual placeholder for future database integration.
 */
class RegisterActivity : AppCompatActivity() {

    /**
     * Initializes the registration UI and sets up dummy logic for the registration process.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bind the Activity to the registration XML layout
        setContentView(R.layout.activity_register)

        // Find the "Back to Login" text link and the "Register" button in the layout
        val btnLoginLink = findViewById<TextView>(R.id.text_back_to_login)
        val btnRegister = findViewById<Button>(R.id.btn_register_account)

        // Configure the back link to close this activity and return to the previous screen (LoginActivity)
        btnLoginLink.setOnClickListener {
            // Terminates the current activity lifecycle
            finish()
        }

        // Configure the registration button to simulate a data transmission process
        btnRegister.setOnClickListener {
            // Display a Toast message to the user simulating a background "Uplink" process
            Toast.makeText(this, "INITIALIZING DATA UPLINK...", Toast.LENGTH_SHORT).show()

            // NOTE: The actual persistence logic (SQLite/Room) is scheduled for the next development sprint.
            // Currently, no data is actually saved here.
        }
    }
}