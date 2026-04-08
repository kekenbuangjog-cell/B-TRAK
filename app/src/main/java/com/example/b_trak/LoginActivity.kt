package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 1. Reference all views
        val statusLabel = findViewById<TextView>(R.id.status_label)
        val inputUser = findViewById<EditText>(R.id.input_user)
        val inputPass = findViewById<EditText>(R.id.input_pass)
        val btnLogin = findViewById<Button>(R.id.btn_initiate)
        val btnRegister = findViewById<TextView>(R.id.text_register)

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val username = inputUser.text.toString().trim()
            val password = inputPass.text.toString().trim()

            // 2. Simple Validation Check
            if (username.isEmpty() || password.isEmpty()) {
                // If fields are empty
                statusLabel.text = "STATUS: ERROR_EMPTY_FIELDS"
                statusLabel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
            }
            else if (username == "admin" && password == "1234") {
                // Hardcoded check for the prototype
                statusLabel.text = "STATUS: ACCESS_GRANTED"
                statusLabel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light))

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                // If credentials don't match
                statusLabel.text = "STATUS: AUTH_FAILED_INVALID_CRED"
                statusLabel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))

                // Optional: Clear password field on fail
                inputPass.setText("")
            }
        }
    }
}