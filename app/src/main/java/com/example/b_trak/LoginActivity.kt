package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * LoginActivity handles the initial user authentication gateway.
 * In this prototype version, it relies on static, hardcoded credentials to demonstrate flow.
 */
class LoginActivity : AppCompatActivity() {
    
    /**
     * Initializes the activity, binds UI elements, and sets up click listeners for navigation and validation.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bind this Activity to its corresponding XML visual layout
        setContentView(R.layout.activity_login)

        // Locate and bind references to the various text fields and buttons on the screen
        val statusLabel = findViewById<TextView>(R.id.status_label)
        val inputUser = findViewById<EditText>(R.id.input_user)
        val inputPass = findViewById<EditText>(R.id.input_pass)
        val btnLogin = findViewById<Button>(R.id.btn_initiate)
        val btnRegister = findViewById<TextView>(R.id.text_register)

        // Configure the "Register" text button to navigate the user to the account creation screen
        btnRegister.setOnClickListener {
            // Create an explicit intent targeting the RegisterActivity class
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent) // Execute the navigation
        }

        // Configure the main "Login" button to process user inputs
        btnLogin.setOnClickListener {
            // Extract the current text from the input fields and strip any leading/trailing whitespace
            val username = inputUser.text.toString().trim()
            val password = inputPass.text.toString().trim()

            // Validate that the user hasn't left the fields completely blank
            if (username.isEmpty() || password.isEmpty()) {
                // Update the status label to show an error message
                statusLabel.text = "STATUS: ERROR EMPTY FIELDS"
                // Change the text color to red to indicate a failure state
                statusLabel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
            }
            // Execute the hardcoded authentication check (username: admin, password: 1234)
            else if (username == "admin" && password == "1234") {
                // If credentials match, update the status label to reflect success
                statusLabel.text = "STATUS: ACCESS GRANTED"
                // Change the text color to green to indicate success
                statusLabel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light))

                // Prepare to transition the user into the core application hub
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent) // Launch the MainActivity
                
                // Destroy the LoginActivity so pressing the back button doesn't log the user out unintentionally
                finish()
            }
            else {
                // Handle the scenario where input was provided but did not match the hardcoded credentials
                statusLabel.text = "STATUS: AUTH FAILED INVALID CRED"
                statusLabel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                
                // Clear the password field for security and convenience
                inputPass.setText("")
            }
        }
    }
}