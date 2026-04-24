package com.example.b_trak

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * REGISTRATION_PROTOCOL: Placeholder for account creation services.
 * Currently serves as a UI prototype for account expansion.
 */
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // UI_BINDING: Identify return navigation and registration trigger
        val btnLoginLink = findViewById<TextView>(R.id.text_back_to_login)
        val btnRegister = findViewById<Button>(R.id.btn_register_account)

        // NAVIGATION: Return to the Authentication Station
        btnLoginLink.setOnClickListener {
            // PROTOCOL: Termination of current context to reveal previous Activity
            finish()
        }

        // ACTION: Initiate Registration Protocol (Placeholder)
        btnRegister.setOnClickListener {
            // STATUS: Mocking data uplink for the prototype
            Toast.makeText(this, "INITIALIZING_DATA_UPLINK...", Toast.LENGTH_SHORT).show()

            // FUTURE_INTEGRATION: SQLite/Room database implementation scheduled for Sprint 2.
        }
    }
}