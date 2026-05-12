package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * LoginActivity handles user authentication against the Room database.
 */
class LoginActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.text = "STATUS: ERROR EMPTY FIELDS"
                statusLabel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(this@LoginActivity)
                val user = db.userDao().getUserByUsername(username)

                if (user != null && user.password == password) {
                    statusLabel.text = "STATUS: ACCESS GRANTED"
                    statusLabel.setTextColor(ContextCompat.getColor(this@LoginActivity, android.R.color.holo_green_light))

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("USER_ID", user.id)
                    startActivity(intent)
                    finish()
                } else {
                    statusLabel.text = "STATUS: AUTH FAILED INVALID CRED"
                    statusLabel.setTextColor(ContextCompat.getColor(this@LoginActivity, android.R.color.holo_red_light))
                    inputPass.setText("")
                }
            }
        }
    }
}