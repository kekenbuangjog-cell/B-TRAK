package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * AUTH_PROTOCOL: LoginActivity manages user authentication.
 * For the prototype version, it employs a hardcoded authentication logic.
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // UI_BINDING: Connect all authentication elements
        val statusLabel = findViewById<TextView>(R.id.status_label)
        val inputUser = findViewById<EditText>(R.id.input_user)
        val inputPass = findViewById<EditText>(R.id.input_pass)
        val btnLogin = findViewById<Button>(R.id.btn_initiate)
        val btnRegister = findViewById<TextView>(R.id.text_register)

        // NAVIGATION: Transition to Account Registration station
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // ACTION: Execute Authentication Protocol
        btnLogin.setOnClickListener {
            val username = inputUser.text.toString().trim()
            val password = inputPass.text.toString().trim()

            // VALIDATION: Input check
            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.text = "STATUS: ERROR_EMPTY_FIELDS"
                statusLabel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
            }
            // PROTOCOL: Hardcoded check for prototype access
            else if (username == "admin" && password == "1234") {
                statusLabel.text = "STATUS: ACCESS_GRANTED"
                statusLabel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light))

                // SYSTEM_TRANSITION: Launch Main Station and terminate Login Activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                // EXCEPTION: Authentication Failure
                statusLabel.text = "STATUS: AUTH_FAILED_INVALID_CRED"
                statusLabel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                inputPass.setText("")
            }
        }
    }
}